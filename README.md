# Trading Bot

## Building and Running Instructions

Solution is implemented in Java. Hence, to build and run it one need to have *Java8* installed. Optionally, you can have Maven installed. Maven Wrapper is checked in into repo, if you don't have maven you can use it.

Go into folder where you extracted the archive and run:

```
./mvnw clean package
```

The command downloads maven(if required, only first time), builds workbook _trading-bot.jar_ . If everything goes well, last lines in the console will be

```
[INFO] --- spring-boot-maven-plugin:1.5.10.RELEASE:repackage (default) @ tradingbot ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 4.571 s
[INFO] Finished at: 2018-02-22T10:00:02+01:00
[INFO] Final Memory: 30M/323M
```

## Running Trading Bot
After building _trading-bot.jar_ one can run it by passing 4 params into command line:

* The product id (see below for a suggested list to test with)
* The buy price
* The upper limit sell price this is the price you are willing to close a position and make a profit.
* The lower limit sell price this the price you want are willing to close a position at and make a loss.

For example:

```
bash-4.4$ java -jar target/trading-bot.jar -productId sb26493 -buy 12387.3 -upper 12401.5 -low 12387.1
``` 
If everying goes well, trading bot will start trading and the following will be printed into the console:

```
10:03:49.330 [main] INFO  o.s.c.a.AnnotationConfigApplicationContext - Refreshing org.springframework.context.annotation.AnnotationConfigApplicationContext@7291c18f: startup date [Thu Feb 22 10:03:49 CET 2018]; root of context hierarchy
10:03:50.095 [main] INFO  org.eclipse.jetty.util.log - Logging initialized @1521ms to org.eclipse.jetty.util.log.Slf4jLog
10:03:50.244 [main] INFO  g.a.tradingbot.NonBlockingTradingBot - started trading with TradingBotSettings(productId=sb26493, buyPrice=12387.3, upperSellPrice=12401.5, lowSellPrice=12387.1, investingAmount=InvestingAmount(currency=BUX, decimals=2, amount=200.00))
10:03:50.729 [main] INFO  g.assignment.WebSocketQuoteFeeder - Connected  to : wss://rtf.beta.getbux.com/subscriptions/me
...
```
## Configuring Trading Bot
In order change address of the quote feed and configure trading endpoint, go to _resources/application.properties_, modify required params, rebuild and restart bot:

```
quote.feed.url=wss://rtf.beta.getbux.com/subscriptions/me
buy.url=https://api.beta.getbux.com/core/16/users/me/trades
sell.url=https://api.beta.getbux.com/core/16/users/me/portfolio/positions/
```

## Error handling
* if command params are missing or invalid, error message is printed into console and program exits, e.g.

```
bash-4.4$ java -jar target/trading-bot.jar -productId sb26493 -buy 12387.3 -upper 12401.5 -low 19387.1
Exception in thread "main" java.lang.reflect.InvocationTargetException
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:497)
	at org.springframework.boot.loader.MainMethodRunner.run(MainMethodRunner.java:48)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:87)
	at org.springframework.boot.loader.Launcher.launch(Launcher.java:50)
	at org.springframework.boot.loader.JarLauncher.main(JarLauncher.java:51)
Caused by: getbux.assignment.tradingbot.IncorrectTradingBotSettingsException: buyPrice must be inside (lowSellPrice, upperSellLimit) range
	at getbux.assignment.tradingbot.TradingBotSettings.<init>(TradingBotSettings.java:28)
	at getbux.assignment.TradingBotApp.parseSettingFromCommandLine(TradingBotApp.java:64)
	at getbux.assignment.TradingBotApp.main(TradingBotApp.java:18)
	... 8 more
```


