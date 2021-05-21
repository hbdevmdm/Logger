Logger
========================

This library is creted for dumping logs of application so developers can easily troubleshoot issues which affect to users.
It covers following types of logs

- Network Logs (Retrofit support)
- Crash Logs (includes device property dump like ram usage, memory usage , device language ...)
- Custom Log (you can dump custom log like activity lifecycle, events , ..)
  it devides in 3 sub type WARNING, DEBUG , INFO log

All you need to intialize logger in your application by
Logger.initializeSession(Context context)


Dump Network Log
========================
and to dump network log you need to attach interceptor by calling addInterceptor method of okhttp client
okhttpClientBuilder.addInterceptor(LoggerInterceptor())

or if you're using retroAPI library you cam use it by
retrofitClientBuilder.addInterceptor(LoggerInterceptor())

but make sure use interceptor sequence wisely.
e.g if you already have interceptor and they're modifying request then use this interceptor just after it.
otherwise you'll have wrong/missing request/response log and it would very difficult to troubleshoot network issues

e.g if you're using retroAPI library then RequestInterceptor() class is modifying request / altering request body and header params.
so it will be safe if you use LoggerInterceptor() just after it. if you use before it then you might have wrong request/response.

e.g retrofitClientBuilder
         .addInterceptor(RequestInterceptor(...))
         .addInterceptor(LoggerInterceptor())
         .build()
         .create(ApiService.class)

