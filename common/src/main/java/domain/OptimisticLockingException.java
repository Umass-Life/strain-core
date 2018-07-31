package domain;


public class OptimisticLockingException extends RuntimeException{
    public OptimisticLockingException(String msg){
        super(msg);
    }
}
