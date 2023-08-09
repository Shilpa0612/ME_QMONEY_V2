
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException
      {
        if(from.compareTo(to) >= 0)
        {
          throw new RuntimeException();
        }
        ObjectMapper om = getObjectMapper();
        
        String url = buildUri(symbol, from, to);
        String response = this.restTemplate.getForObject(url,String.class);
        TiingoCandle[] tingoCandle =  om.readValue(response,TiingoCandle[].class);
        if(tingoCandle == null)
          return new ArrayList<Candle>();
        else
        {
          List <Candle> list = Arrays.asList(tingoCandle);
          return list;
        }
        
      }

      protected static String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
        String uriTemplate = "https://api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
          String token = "742d992a016d41e5b66a9e6306739eea38a393e1";
          String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
          .replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());

          return url;        
      }
}
