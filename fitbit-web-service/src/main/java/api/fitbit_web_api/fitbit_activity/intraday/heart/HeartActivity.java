package api.fitbit_web_api.fitbit_activity.intraday.heart;

import api.fitbit_web_api.fitbit_activity.intraday.AbstractIntradayActivity;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"fitbit_user_id", "date_time" })}
)
public class HeartActivity extends AbstractIntradayActivity{
    public HeartActivity(){}
}
