package util;

public class StrainTimer {

    public long st;
    ColorLogger logger;
    String name = null;
    public StrainTimer(ColorLogger logger){this.logger = logger;}
    public StrainTimer(ColorLogger logger, String name){
        this(logger);
        this.name=name;
    }
    public void start(){
        st = System.currentTimeMillis();
    }

    public void stop(){
        logger.info(this.name + " TIME: " + (System.currentTimeMillis() - st) + " ms");
        st = 0;
    }
}
