package api.data_ingestion.fitbit_batch;

import api.fitbit_account.fitbit_auth.FitbitAuthenticationService;
import api.fitbit_account.fitbit_user.FitbitUser;
import api.fitbit_account.fitbit_user.FitbitUserService;
import api.fitbit_web_api.FitbitWebApiController;
import api.fitbit_web_api.fitbit_activity.aggregate.AggregateActivityService;
import api.fitbit_web_api.fitbit_activity.intraday.IntradayActivityService;
import api.fitbit_web_api.fitbit_heartrate.FitbitHeartrateAPIService;
import api.fitbit_web_api.fitbit_sleep.FitbitSleepAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import util.ColorLogger;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

@Service
public class BatchProcessingService {
    private final Logger logger = Logger.getLogger(FitbitWebApiController.class.getSimpleName());
    private final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private AggregateActivityService aggregateActivityService;
    @Autowired
    private IntradayActivityService intradayActivityService;
    @Autowired
    private FitbitHeartrateAPIService heartrateService;
    @Autowired
    private FitbitSleepAPIService sleepService;
    @Autowired
    private FitbitUserService fitbitUserService;
    @Autowired
    private FitbitAuthenticationService fitbitAuthenticationService;

    public void ingest(){
        Iterable<FitbitUser> itr = fitbitUserService.list();
        List<FitbitUser> users = new ArrayList<FitbitUser>();
        for(FitbitUser user : itr){
            users.add(user);
        }
        this.ingest(users);
    }

    public void ingest(FitbitUser user){
        List<FitbitUser> users = Arrays.asList(user);
        this.ingest(users);
    }

    public void ingest(List<FitbitUser> users){
       ExecutorService threadPool =  Executors.newFixedThreadPool(10);

       LinkedBlockingQueue<List<FetchTask>> taskBatchQ = new LinkedBlockingQueue();
       LocalDateTime now = LocalDateTime.now();
       // change start date here.
//       LocalDateTime cur = LocalDateTime.of(2018, Month.AUGUST, 18, 0, 0);
        LocalDateTime cur = now.minusDays(99);
       while(now.isAfter(cur)){
           String d1 = FitbitAuthenticationService.toRequestDateFormat(cur);
           LocalDateTime to = cur.plusDays(10);
           if (to.isAfter(now)) to = now;
           String d2 = FitbitAuthenticationService.toRequestDateFormat(to);
           boolean save = true;
           colorLog.info("DEBUG---> %s | %s", d1, d2);
           List<FetchTask> taskBatch = new ArrayList<>();
           users.forEach(fitbitUser -> {
               colorLog.info("fetchBatch for User: " + fitbitUser.getFitbitId());
               String sleepTask = String.format("Sleep: %s-[%s]-[%s]", fitbitUser.getFitbitId(), d1, d2);
               FetchTask sleepFetch = FetchTask.of(sleepTask, () -> {
                   colorLog.info(sleepTask);
                   try {
                       Object obj = sleepService.fetchAndSave(fitbitUser, d1, d2, save);
                   } catch(Exception e){
                       e.printStackTrace();
                       colorLog.severe("ERR[%s %s]: %s", fitbitUser.getFitbitId(), sleepTask, e.getMessage());
                   }
               });
               String hrTask = String.format("HR: %s-[%s]-[%s]", fitbitUser.getFitbitId(), d1, d2);
               FetchTask hrFetch = FetchTask.of(hrTask, () -> {
                   colorLog.info(hrTask);
                   try {
                       Object obj = heartrateService.fetchAndSave(fitbitUser, d1, d2, save);
                   } catch(Exception e){
                       e.printStackTrace();
                       colorLog.severe("ERR[%s %s]: %s", fitbitUser.getFitbitId(), hrTask, e.getMessage());
                   }
               });

               String aggrename = String.format("Aggre: %s-[%s]-[%s]", fitbitUser.getFitbitId(), d1, d2);
               FetchTask agreFetch = FetchTask.of(aggrename, () -> {
                   colorLog.info(aggrename);
                   try {
                       Object obj = aggregateActivityService.fetchAndSave(fitbitUser,
                               aggregateActivityService.getActivitiesResources()
                               ,d1, d2, save);
                   } catch(Exception e){
                       e.printStackTrace();
                       colorLog.severe("ERR[%s %s]: %s", fitbitUser.getFitbitId(), aggrename, e.getMessage());
                   }
               });

               String intraname = String.format("Intra: %s-[%s]-[%s]", fitbitUser.getFitbitId(), d1, d2);
               FetchTask intraFetch = FetchTask.of(intraname, () -> {
                   colorLog.info(intraname);
                   try {
                       Object obj = intradayActivityService.fetchAndSave(fitbitUser,
                               intradayActivityService.getActivitiesResources()
                               ,d1, d2, save);
                   } catch(Exception e){
                       e.printStackTrace();
                       colorLog.severe("ERR[%s %s]: %s", fitbitUser.getFitbitId(), intraname, e.getMessage());
                   }
               });

               taskBatch.add(sleepFetch);
               taskBatch.add(hrFetch);
               taskBatch.add(agreFetch);
               taskBatch.add(intraFetch);
           });
           taskBatchQ.add(taskBatch);
           cur = to;
       }

       try {
           Timer timer = new Timer("Timer");
           TimerTask repeatedTask = new TimerTask() {
               public void run() {
    //                    System.out.println("Task performed on " + new Date());
                   List<FetchTask> taskBatch = taskBatchQ.poll();
                   taskBatch.forEach(task -> {
                       threadPool.execute(task.getTask());
                   });

                   if (taskBatchQ.isEmpty()){
                       this.cancel();
                       timer.cancel();
                       return;
                   }
               }
           };


           long delay  = 1000L;
           long period = 1000L * 60L * 61L; // 1000 ms/sec x 60 sec/min x 64 min
           timer.scheduleAtFixedRate(repeatedTask, delay, period);

       } catch(Exception e){
           e.printStackTrace();
           colorLog.severe(e.getMessage());

       }
    }

    public static class FetchTask {
        private String name;
        private Runnable task;
        public static FetchTask of(String name, Runnable f){
            FetchTask ft = new FetchTask();
            ft.setName(name);
            ft.setTask(f);
            return ft;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Runnable getTask() {
            return task;
        }

        public void setTask(Runnable task) {
            this.task = task;
        }
    }


}
