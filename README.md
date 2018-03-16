# Backend Developer Assignment
## Introduction
The BUX mobile apps communicate with the BUX backend through an HTTP RESTful API and messages exchanged over a WebSocket
connection.
The data format of the REST API is structured in JSON, as well as the messages exchanged over WebSocket.
On top of the WebSocket connection, we have created an application protocol based on the concept of real-time feed "channels". The client can
subscribe to these channels in order to receive real-time updates for it.
We leverage the 'full-duplex' nature of WebSockets: the client has to send a WebSocket message to subscribe for a channel, and from this
moment on, until unsubscription (or disconnect), he will start receiving messages with updates on this channel over the WebSocket connection.

## Assignment Requirements
The goal of this assignment is to build a very basic Trading Bot that tracks the price of a certain product and will execute a pre-defined trade in
said product when it reaches a given price. After the product has been bought the Trading Bot should keep on tracking the prices and execute a
sell order when a certain price is hit. In the input there should be a "upper limit" price and a "lower limit" price.
At startup the Trading Bot should gather four inputs;
* The product id (see below for a suggested list to test with)
* The buy price
* The upper limit sell price this is the price you are willing to close a position and make a profit.
* The lower limit sell price this the price you want are willing to close a position at and make a loss.

Note that for the trading logic to be valid, the relation between the buy price and the lower / upper limit should be: lower limit sell price < buy price
< upper limit sell price. Think about what it means to close a position with a profit. What should the relation between the current price and the
upper limit selling price should be when deciding to close the position or not?
The Trading Bot should then subscribe to the Websocket channel for the given product id and when the buy price is reached it should execute the
buy order (API definition below) and then when one of the limits is reached it should execute the sell order

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


