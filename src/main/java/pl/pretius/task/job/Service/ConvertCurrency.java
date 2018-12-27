package pl.pretius.task.job.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.catalina.core.ApplicationContext;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.pretius.task.job.Model.RateBean;
import pl.pretius.task.job.controller.Currency;
import pl.pretius.task.job.Model.Rate;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.*;

@Service
public class ConvertCurrency {

    @Autowired
    private RateBean rateBean;

    private static Logger LOG = LoggerFactory.getLogger(ConvertCurrency.class);

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }

        return sb.substring(1,sb.length()-1);
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public Double convert(Currency currency){

        Double money;
        Collection<Rate> rates = null;
        Rate rate = null;
        Rate reteTarget = null;
        if(rateBean.getRates() == null){

            downloadData();
        }
        else {

            rates = rateBean.getRates();
        }
        Optional<Rate> optionalSource = rates.stream().filter(x -> currency.getCurrency().equals(x.getCode()))
                .findFirst();
        if(optionalSource.isPresent()) {
            rate = optionalSource.get();
        }
        Optional<Rate> optionalTarget = rates.stream().filter(x -> currency.getTargetCurrency().equals(x.getCode()))
                .findFirst();
        if(optionalTarget.isPresent()) {
            reteTarget = optionalTarget.get();
        }
        money = rate.getMid() * currency.getAmount() / reteTarget.getMid();

        return money;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void scheduleData() {

        downloadData();
    }

    private void downloadData(){
        try {

            JSONObject json = readJsonFromUrl("http://api.nbp.pl/api/exchangerates/tables/A/?format=json");
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Rate>>(){}.getType();
            Collection<Rate> ratesFormJson = gson.fromJson(json.get("rates").toString(), collectionType);
            rateBean.setRates(ratesFormJson);
        } catch (IOException e){
            LOG.error(e.getMessage());
        } catch (JSONException e){
            LOG.error(e.getMessage());
        }
    }
}
