## Подключение библиотеки к основному проекту:
В build.gradle основного проекта:
```
implementation project(":geopushlib")
```
В settings.gradle основного проекта:
```
include ':geopushlib'
```

## Использование библиотеки:
Работа с библиотекой осуществляется через класс GeoPush.

Необходимо произвести инициализацию библиотеки в Application классе вашего проекта
```
GeoPush.init(applicationContext)
```
После инициализации, вся дальнейшая работа с SDK усуществляется через публичный объект
```
GeoPush.shared()
```

## Для работы пуш сообщений:
Подключить консоль Firebase к основному проекту.
Инициализировать Firebase в методе OnCreate основного activity проекта:
```
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        FirebaseApp.initializeApp(this)
        ...
```
И передать Firebase-токен в GeoPush. 

Для этого необходимо создать сервис, для получения/обновления пуш-токена устройста
```
class FirebaseMessagingService : FirebaseMessagingService(){

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        token?.let { 
            GeoPush.shared().sendPushToken(it) //передача токена в сдк
        }
    }

    override fun onMessageReceived(message: RemoteMessage?) {
       val messageId = message?.data?.let {
            GeoPush.shared().markPushDelivered(it)
        }
        messageId?.let { 
            showGeoPushNotification(getMessageTitle(message) , getMessageText(message), it)
        }?:kotlin.run {
            //default notifications
        }
    }
```
**GeoPush.shared().sendPushToken(it)** - отправка пуш-токена на сервер SDK

**GeoPush.shared().markPushDelivered(it)** - пометить пуш как полученный. Функция возвращает идентификатор сообщения, который необходимо передать на запускаемый по клику экран, для того, чтобы отметить пуш как открытый

**GeoPush.shared().markPushOpened(id)** - пометить пуш как открытый

B прописать его в AndroidManifest.xml основного проекта
```
<service android:name="FirebaseMessagingService">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
```

## Для работы геолокации:
Добавить в AndroidManifest.xml основного проекта:
```
    ...
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>
    ...
    <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="Your google maps api key"/>
    ...
```
В коде основного проекта запросить разрешения на использование геолокации.

Для того, чтобы SDK собирал информацию о местоположении необходимо использовать
```
GeoPush.shared().startTracking()
или
GeoPush.shared().startTracking(true|false)
```
Методы могут вызваны как в Application классе основного проекта(после инициализации SDK), так и в любой момент работы основного проекта

**startTracking()** и 
**startTracking(false)** - запускается отслеживание пользоваталя только если ранее в основном проекте были получены разрешения на местоположение

**startTracking(true)**- запускается отслеживаение пользоваталя если ранее в основном проекте были получены разрешения на местоположение, иначе SDK ожидает получения местоположения и запустит автоматически отслеживание, после их получения

