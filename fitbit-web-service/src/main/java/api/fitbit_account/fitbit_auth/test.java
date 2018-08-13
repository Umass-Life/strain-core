package api.fitbit_account.fitbit_auth;

import util.EntityHelper;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.function.Supplier;

public class test {

    public static void main(String[] args) {


//        System.out.println(FitbitAuthenticationService.toRequestDateFormat(LocalDateTime.now()));
//
//        LocalDateTime d =  FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:00.000");
//        LocalDateTime d2 =  FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:00.000");
//        LocalDateTime d3 = FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:30");
//        LocalDateTime d4 = FitbitAuthenticationService.parseLongTimeParam("2018-08-16T23:59:59");

//        System.out.println(d4);
//        d3 = d3.plusDays(1);
//        System.out.println(d3);
//        System.out.println(d3.getHour());
//        System.out.println(d3.getMinute());
//        System.out.println(d3.getSecond());
//        System.out.println(d.compareTo(d2));
//        System.out.println(FitbitAuthenticationService.toRequestDateFormat(d));
//        String x = null;//new String("a");
//        Optional.of(x).ifPresent(y -> System.out.println(y));
//
        Supplier<Integer> f = () -> {
            int c = 0;
            try {
                if (c==0){
                    c = 1;
//                    return c;
                }
                c = 2;
            } catch(Exception e){
                e.printStackTrace();
            } finally {
                System.out.println("finally");
                return c;
            }
        };
        System.out.println(f.get());

//        Timer timer = new Timer("Timer");
//        TimerTask repeatedTask = new TimerTask() {
//            int cnt = 0;
//            public void run() {
//                System.out.println("Task performed on " + new Date());
//                Date taskDate = new Date();
//                cnt ++;
//                if (cnt > 2) {
//                    this.cancel();
//                    timer.cancel();
//                    return;
//                }
//            }
//        };
//
//
//
//        long delay  = 1000L;
//        long period = 1000L;//;60L * 64L; // 1000 ms/sec x 60 sec/min x 64 min
//        timer.scheduleAtFixedRate(repeatedTask, delay, period);



//        LocalDateTime now = LocalDateTime.now();
//        LocalDateTime cur = LocalDateTime.of(2018, Month.JUNE, 1, 0, 0);
//        while(now.isAfter(cur)){
//            String d1 = FitbitAuthenticationService.toRequestDateFormat(cur);
//            LocalDateTime to = cur.plusDays(7);
//            if (to.isAfter(now)) to = now;
//            String d2 = FitbitAuthenticationService.toRequestDateFormat(to);
//            Arrays.asList(1,2).forEach(fitbitUser -> {
//                String taskName = String.format("%s-[%s]-[%s]", fitbitUser, d1, d2);
//                System.out.println(taskName);
//
//            });
//            cur = to;
////            FetchTask task = FetchTask.of()
//        }


    }
}
