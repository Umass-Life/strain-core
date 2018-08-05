package api.fitbit_account.fitbit_auth;

import util.EntityHelper;

import java.time.LocalDateTime;
import java.util.Optional;

public class test {

    public static void main(String[] args) {


//        System.out.println(FitbitAuthenticationService.toRequestDateFormat(LocalDateTime.now()));

        LocalDateTime d =  FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:00.000");
        LocalDateTime d2 =  FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:00.000");
        LocalDateTime d3 = FitbitAuthenticationService.parseLongTimeParam("2018-08-16T08:15:00");
        System.out.println(d3);
        d3 = d3.plusDays(1);
        System.out.println(d3);
//        System.out.println(d.compareTo(d2));
//        System.out.println(FitbitAuthenticationService.toRequestDateFormat(d));
//        String x = null;//new String("a");
//        Optional.of(x).ifPresent(y -> System.out.println(y));
    }
}
