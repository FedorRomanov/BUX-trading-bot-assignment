package getbux.assignment;


import getbux.assignment.tradingbot.TradingBotSettings;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class TradingBotApp {

    public static void main(String[] args) throws ParseException {
        TradingBotAppConfig.settings = parseSettingFromCommandLine(args);

        ApplicationContext context = new AnnotationConfigApplicationContext(TradingBotAppConfig.class);
        WebSocketQuoteFeeder quoteFeeder = (WebSocketQuoteFeeder) context.getBean("quoteFeeder");

        quoteFeeder.startFeeding();
  }

  static TradingBotSettings parseSettingFromCommandLine(String[] args) throws ParseException {
      Options options = new Options();
      CommandLineParser parser = new DefaultParser();
      Option productIdOpt = Option.builder("productId")
              .longOpt( "product-identificator" )
              .desc( "productId to trade, e.g. sb26493."  )
              .hasArg()
              .argName( "PRODUCT_ID" )
              .build();
      Option buyPriceOpt = Option.builder("buy")
              .longOpt( "buy-price" )
              .desc( "The buy price."  )
              .hasArg()
              .argName( "PRICE" )
              .build();
      Option upperLimitOpt = Option.builder("upper")
              .longOpt( "upper-limit-sell-price" )
              .desc( "The upper limit sell price you are willing to close a position and make a profit."  )
              .hasArg()
              .argName( "PRICE" )
              .build();
      Option lowLimitOpt = Option.builder("low")
              .longOpt( "low-limit-sell-price" )
              .desc( "The lower limit sell price you want are willing to close a position at and make a loss."  )
              .hasArg()
              .argName( "PRICE" )
              .build();
      options.addOption(productIdOpt);
      options.addOption(buyPriceOpt);
      options.addOption(upperLimitOpt);
      options.addOption(lowLimitOpt);
      CommandLine cmd = parser.parse( options, args);
      if(!cmd.hasOption("productId") || !cmd.hasOption("buy") || !cmd.hasOption("upper") || !cmd.hasOption("low") ){
          help(options);
      }
      return new TradingBotSettings(cmd.getOptionValue("productId"),
                        cmd.getOptionValue("buy"),
                        cmd.getOptionValue("upper"),
                        cmd.getOptionValue("low"));
  }

    static void help(Options options) {
        HelpFormatter formater = new HelpFormatter();
        formater.printHelp("TradingBotApp", options);
        System.exit(0);
    }


}
