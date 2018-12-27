package pl.pretius.task.job.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import pl.pretius.task.job.Service.ConvertCurrency;

@RestController
public class CurrencyController {

    private ConvertCurrency convertCurrency;

    @Autowired
    public CurrencyController(ConvertCurrency convertCurrency) {
        this.convertCurrency= convertCurrency;
    }

    @RequestMapping(method = RequestMethod.POST)
    public Double exchange(@RequestBody Currency currency){
        ;
        return convertCurrency.convert(currency);
    }


}
