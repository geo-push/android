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
GeoPush.init(applicationContext, useDevServer, logEnabled)
```
**useDevServer: Boolean.** Использовать дев сервер в SDK. Значение по-умолчанию false.

**logEnabled: Boolean.** Включение/выключение логирования. Значение по-умолчанию false.

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

Для этого необходимо создать сервис, для получения/обновления пуш-токена устройства
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
            //Пуш получен от SDK. Необходимо его показать и передать messageId в intent запускаемого экрана. 
        }?:kotlin.run {
            //other notifications
        }
    }
```
**GeoPush.shared().sendPushToken(token)** - отправка пуш-токена на сервер SDK

**GeoPush.shared().markPushDelivered(message.data)** - пометить пуш как полученный. Функция возвращает идентификатор сообщения, который необходимо передать на запускаемый по клику экран, для того, чтобы отметить пуш как открытый

**GeoPush.shared().markPushOpened(messageId)** - пометить пуш как открытый

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

**true|false** - настройка, указывающая стоит ли SDK мониторить разрешения на местоположение. 

**startTracking()** и 
**startTracking(false)** - запускается отслеживание пользоваталя только если ранее в основном проекте были получены разрешения на местоположение

**startTracking(true)**- запускается отслеживаение пользоваталя если ранее в основном проекте были получены разрешения на местоположение, иначе SDK ожидает получения местоположения и запустит автоматически отслеживание, после их получения


## Настройка отправки данных о пользователе:
```
 var map = HashMap<String, Any>()
            map.put("name", "Alex")
            map.put("age", 21)
            map.put("birthdate", "12.12.2019")
            map.put("isMarried", true)
GeoPush.shared().sendUserInfo(map)
```

