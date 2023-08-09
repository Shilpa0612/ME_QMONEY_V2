
package com.crio.warmup.stock.portfolio;

// import static java.time.temporal.ChronoUnit.DAYS;
// import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
// import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
// import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
// import java.util.concurrent.ExecutionException;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.Future;
// import java.util.concurrent.TimeUnit;
// import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {

RestTemplate restTemplate;


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  StockQuotesService stockQuotesService;
  PortfolioManagerImpl(StockQuotesService stockQuotesService)
  {
    this.stockQuotesService= stockQuotesService;
  }

  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,  StockQuoteServiceException {
        
      return stockQuotesService.getStockQuote(symbol, from, to);
  }

  /*protected static String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
            + "startDate=$STARTDATE&endDate=$ENDDATE&token=$APIKEY";
          String token = "742d992a016d41e5b66a9e6306739eea38a393e1";
          String url = uriTemplate.replace("$APIKEY", token).replace("$SYMBOL", symbol)
          .replace("$STARTDATE", startDate.toString()).replace("$ENDDATE", endDate.toString());

          return url;
  }
*/
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
      // TODO Auto-generated method stub
      AnnualizedReturn annualizedReturn;
      List <AnnualizedReturn> list = new ArrayList<>();
      for(int i = 0; i < portfolioTrades.size(); i++)
      {
        annualizedReturn = getAnnualizedReturn(portfolioTrades.get(i),endDate);
        list.add(annualizedReturn);
      }
      Comparator <AnnualizedReturn> comparator = Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
      Collections.sort(list, comparator);
    return list;
  }

  public AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endDate){
    String symbol = trade.getSymbol();
    AnnualizedReturn annualizedReturn = null;
    LocalDate startDate = trade.getPurchaseDate();
    try
    {
      List <Candle> list = getStockQuote(symbol, startDate, endDate);
    
      Candle firstStock = list.get(0);
      Candle last = list.get(list.size() - 1);
      
      double buyPrice = firstStock.getOpen();
      double sellPrice = last.getClose();
  
      double totalReturn = (sellPrice - buyPrice)/buyPrice;
      double numYears = (double) ChronoUnit.DAYS.between(startDate, endDate)/365;

      double annualizedReturns = Math.pow((1 + totalReturn), (1 / numYears)) - 1;
      annualizedReturn = new AnnualizedReturn(symbol, annualizedReturns, totalReturn);
    }
    catch(JsonProcessingException e)
    {
        annualizedReturn = new AnnualizedReturn(symbol, Double.NaN, Double.NaN);
    }
    catch(StockQuoteServiceException e)
    {
      new StockQuoteServiceException("NullPointerException Thrown");
    }
    return annualizedReturn;
  }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
