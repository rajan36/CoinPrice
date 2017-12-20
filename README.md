# CryptoPrice Project<br />

<br />
<br />
This project is about making an android application which will help in quickly get prices of cryptocurrencies from different platforms and give notification to user if it goes over/down some threshold. This app will also help in maintaining portfolio.

## Features this app has....(Non Technical)
- User can see prices of cryptocurrencies from different platforms. (High Low(Last 24 hrs) Current)
- User can maintain their portfolio. Can add coins at different prices and see his portfolio value change as prices fluctuate.
- User can get alert if currency price reach some level.
- User can see prices of their favorite crytocurrencies from their home screen with means of widgets.

## Features this app has... (Some Technical aspects that must cover)<br />
- Every api call must store its result in local database so that we can better analyze the data in future.<br />
- Widgets must be priority since it involves quick action.<br />
- Charts(If all modules work fine.After that develop this) <br />

## Dev Log:
   #### Day 1, 3 December 2017 8:38 PM<br />
* Goals<br />
   - Setup Git repository<br />
   - Familiarise with Android Devlopment once again.<br />
   - Finalize First page.<br />
   - prepare proof of concepts for getting prices from atleast one exchange platform.<br />
* Achievements<br />
   - Git Repo Up and Running<br />
   - Developed First Page to get Prices data with use of Recycler view<br />
 #### Day 2, 4 December 2017 10:43 PM<br />
* Goals<br/>
   - Call api at every 10 minutes in background<br/>
   - Store api results in sql db <br/>
* Updates <br/>
    - This will take time.... Sleepy
 #### Day 3, 5 December 2017 9:41 PM<br />
* Goals<br/>
   - Yesterday's Goals... <br/>
* Updates </br>
    - Nothing accomplished
 #### Day 4, 10 December 2017 10:56 PM<br />
* Goals<br/>
    - Add support for coinmarketcap api
* Updates<br/>
    - Added support for coinmarketcap api
    - Used Gson for parsing and Volley for requesting
    - Still to remove unnecessary code
 #### Day 5, 11 December 2017 9:58 PM<br />
 * Goals<br/>
    - Run Background Service
 * Updates<br/>
    - Added background service to fetch ticker data from two apis.
 #### Day 6, 18 December 2017 9:29 PM <br/>
 * Goals<br/>
    - Alert for specific price (Send Notification)
    - Schedule Background service to run at specific interval.
 * Updates<br/>
    - Added Jobscheduler which will fetch ticker details every 1 minute.
    - Added NotificationUtils to show simple notification
 * Issues<br/>
    - Notification not visible when called from service.
    - Need to simplify and sanitize code.
    - Need Better Design
 #### Day 7, 19 December 2017 9:32 PM <br/>
 * Goals<br/>
    - Add Settigs Screen to configure interval for refresh.
    - Move refresh button to menu.
    - Save Network response in db.
    - Fill content for Notification
 * Updates<br/>
    - Moved Refresh button to menu
    - Saved raw network response as json in two separate table
    - Changed content for notification
    - Settings Screen and Interval for refresh is still to be done.
    - Notification is visible even if it executes in from scheduler service
 * Issues <br/>
    - Price is matched incorrectly for coinmarketcap ticker
    - Better design needed and need to simplify code
 #### Day 8, 20 December 2017 4:56 PM <br/>
  * Goals <br/>
    - Add Settings screen to configure interval for refresh
    - Map Prices correctly