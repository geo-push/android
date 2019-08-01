## Подключение библиотеки к основному проекту:
В build.gradle основного проекта:
```
implementation project(":geopushlib")
```
В settings.gradle основного проекта:
```
include ':geopushlib'
```
Добавить в AndroidManifest.xml основного проекта:
```
<uses-permission android:name="android.permission.FOREGROUND_SERVICE"/>
<uses-permission android:name="android.permission.INTERNET" />
```
## Использование библиотеки:
Основное activity должно быть наследовано от класса GeopushMainActivity
```
class MainActivity : GeopushMainActivity()
```
## Для работы пуш сообщений:
Подключить консоль Firebase к основному проекту.
Инициализировать Firebase в методе OnCreate основного activity приложения:
```
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ...
        FirebaseApp.initializeApp(this)
        ...
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
