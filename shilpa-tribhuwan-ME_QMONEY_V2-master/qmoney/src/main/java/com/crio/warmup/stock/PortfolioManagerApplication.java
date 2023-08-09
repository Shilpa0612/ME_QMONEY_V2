
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.io.BufferedReader;
import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
//import java.nio.file.Files;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
//import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Arrays;
//import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;
import java.io.FileReader;

public class PortfolioManagerApplication {

  public static RestTemplate restTemplate = new RestTemplate();
  public static PortfolioManager portfolioManager = PortfolioManagerFactory.getPortfolioManager(restTemplate);

  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    File f = resolveFileFromResources(args[0]);
    // ObjectMapper om = new ObjectMapper();
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] trades = om.readValue(f, PortfolioTrade[].class);
    List<String> symbols = new ArrayList<>();

    for (PortfolioTrade trade : trades) {
      System.out.println(trade.getSymbol());
      symbols.add(trade.getSymbol());
    }
    return symbols;
  }



 

  // Note:
  // 1. You may need to copy relevant code from #mainReadQuotes to parse the Json.
  // 2. Remember to get the latest quotes from Tiingo API.

  // TODO: CRIO_TASK_MODULE_REST_API
  // Find out the closing price of each stock on the end_date and return the list
  // of all symbols in ascending order by its close value on end date.

  // Note:
  // 1. You may have to register on Tiingo to get the api_token.
  // 2. Look at args parameter and the module instructions carefully.
  // 2. You can copy relevant code from #mainReadFile to parse the Json.
  // 3. Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note:
  // Remember to confirm that you are getting same results for annualized returns
  // as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
   // File f = resolveFileFromResources(args[0]);
    // ObjectMapper om = new ObjectMapper();
    ObjectMapper om = getObjectMapper();
    List<PortfolioTrade> trades = readTradesFromJson(args[0]);
    String endDate = args[1];

    RestTemplate restTemplate = new RestTemplate();
    //restTemplate.
    List <TotalReturnsDto> list = new ArrayList<>();
    List <String> soretdList = new ArrayList<>();

    for(PortfolioTrade trade : trades)
    {
      String url = prepareUrl(trade, LocalDate.parse(args[1]), getToken());
      TiingoCandle[] tiingo = restTemplate.getForObject(url, TiingoCandle[].class);
      if(tiingo != null)
      {
        list.add(new TotalReturnsDto(trade.getSymbol(), tiingo[tiingo.length - 1].getClose()));
      }
    }
    //CustomComparator comparator = new CustomComparator();
    Comparator <TotalReturnsDto> sortByClosePrice = Comparator.comparing(TotalReturnsDto::getClosingPrice);
  Collections.sort(list, sortByClosePrice);

    for(TotalReturnsDto obj : list)
    {
      soretdList.add(obj.getSymbol());
    }
    return soretdList;
  }

  

  // TODO:
  // After refactor, make sure that the tests pass by using these two commands
  // ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  // ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    List<PortfolioTrade> list = new ArrayList<>();
    File file = resolveFileFromResources(filename);
    ObjectMapper om = getObjectMapper();
    PortfolioTrade[] obj = om.readValue(file, PortfolioTrade[].class);
    for (PortfolioTrade i : obj) {
      list.add(i);
    }
    return list;
  }

  // TODO:
  // Build the Url using given parameters and use this function in your code to
  // cann the API.
  static String getToken() {
    // return "530e130e7528ce50272c2ee31f90acefd925df6f";
    return "742d992a016d41e5b66a9e6306739eea38a393e1";
  }
  

  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
    return "https://api.tiingo.com/tiingo/daily/" + trade.getSymbol() + "/prices?startDate=" + trade.getPurchaseDate()
        + "&endDate=" + endDate + "&token=" + token;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    // ObjectMapper mapper = new ObjectMapper();
    ObjectMapper mapper = getObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0 (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/shilpa-tribhuwan-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@2f9f7dcf";
    String functionNameFromTestFileInStackTrace = "PortfolioManagerApplicationTest.mainReadFile()";
    String lineNumberFromTestFileInStackTrace = "29:1";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace });
  }




  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
  static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    double price = candles.get(0).getOpen();    
     return price;
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     //return 0.0;
     //int length = candles.size();
     double price = candles.get(candles.size() - 1).getClose();    
     return price;
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate = new RestTemplate();
    String url = prepareUrl(trade, endDate, getToken());
    TiingoCandle[] tiingo = restTemplate.getForObject(url, TiingoCandle[].class);
    List <Candle> list = new ArrayList<>();
    for(TiingoCandle t : tiingo)
    {
      list.add(t);
    }
    return list;
  }

   // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Now that you have the list of PortfolioTrade and their data, calculate annualized returns
  //  for the stocks provided in the Json.
  //  Use the function you just wrote #calculateAnnualizedReturns.
  //  Return the list of AnnualizedReturns sorted by annualizedReturns in descending order.
  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
        List <AnnualizedReturn> list = new ArrayList<>();
        File trade = resolveFileFromResources(args[0]);
        LocalDate endDate = LocalDate.parse(args[1]);
        ObjectMapper om = getObjectMapper();

        PortfolioTrade [] tradeObject = om.readValue(trade, PortfolioTrade[].class);
        for(int i = 0; i < tradeObject.length;i++)
        {
          list.add(getAnnualizedReturn(tradeObject[i] ,endDate));
        }
        Comparator <AnnualizedReturn> sortByAnnRet = Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
     Collections.sort(list, sortByAnnRet);
     return list;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        double totalReturn = (sellPrice - buyPrice)/buyPrice;
        double numYears = ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate) / 365.24;
        double annualizedReturns = Math.pow((1 + totalReturn),(1/numYears)) - 1;
      return new AnnualizedReturn(trade.getSymbol(), annualizedReturns, totalReturn);
  }




public static AnnualizedReturn getAnnualizedReturn(PortfolioTrade trade, LocalDate endDate){
  String symbol = trade.getSymbol();
  LocalDate startDate = trade.getPurchaseDate();
  if(startDate.compareTo(endDate) >= 0)
  {
    throw new RuntimeException();
  }
  String url = prepareUrl(trade, endDate, getToken());
  RestTemplate restTemplate = new RestTemplate();

  TiingoCandle []tiingo = restTemplate.getForObject(url,TiingoCandle[].class);
  if(tiingo != null)
  {
    TiingoCandle firstStock = tiingo[0];
    TiingoCandle last = tiingo[tiingo.length - 1];
    
    double buyPrice = firstStock.getOpen();
    double sellPrice = last.getClose();

    AnnualizedReturn annReturn = calculateAnnualizedReturns(endDate, trade, buyPrice, sellPrice);
    return annReturn;
  }
  else
  {
    return new AnnualizedReturn(symbol,Double.NaN,Double.NaN);
  }
}



  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       String contents = readFileAsString(file);
       ObjectMapper objectMapper = getObjectMapper();
       PortfolioTrade [] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }


  private static String readFileAsString(String file) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(file));
    StringBuilder stringBuilder = new StringBuilder();
    String line = null;
    String lineSeparator = System.getProperty("line.separator");
    while((line = br.readLine()) != null)
    {
      stringBuilder.append(line);
      stringBuilder.append(lineSeparator);
    }
    stringBuilder.deleteCharAt(stringBuilder.length() - 1);
    return stringBuilder.toString();
  }





  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());


//printJsonObject(mainCalculateReturnsAfterRefactor(args));

  }
}

