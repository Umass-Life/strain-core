package util;

public class StrainTimer {

    public long st;
    ColorLogger logger;
    public StrainTimer(ColorLogger logger){this.logger = logger;}
    public void start(){
        st = System.currentTimeMillis();
    }

    public void stop(){
        logger.info("TIME: " + (System.currentTimeMillis() - st) + " ms");
        st = 0;
    }
}
