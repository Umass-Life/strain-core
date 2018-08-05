package api.fitbit_web_api.fitbit_activity.intraday.calories;

import api.fitbit_web_api.fitbit_activity.intraday.AbstractIntradayActivity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fitbit_user_id", "date_time" })}
)
public class CaloriesActivity extends AbstractIntradayActivity {
    public CaloriesActivity(){}

}
