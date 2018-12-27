package pl.pretius.task.job.Model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collection;

@Component
@Scope(value = WebApplicationContext.SCOPE_APPLICATION)
public class RateBean {

    private Collection<Rate> rates;

    public Collection<Rate> getRates() {
        return rates;
    }

    public void setRates(Collection<Rate> rates) {
        this.rates = rates;
    }
}
