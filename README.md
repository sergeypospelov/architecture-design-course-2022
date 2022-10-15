# Архитектура CLI

## Команда

- Светлана Шмидт https://github.com/lana243
- Никита Строганов https://github.com/ideaseeker
- Сергей Поспелов https://github.com/sergeypospelov

## Сборка и запуск

- `gradlew jar`
- `java -jar build/libs/architecture-design-course-2022-1.0-SNAPSHOT.jar`


## Схемы

### Class Diagram
![class-diagram](schemes/class-diagram.png)

### Dataflow Diagram
![dataflow-diagram](schemes/dataflow-diagram.png)

Ниже приведено описание архитектуры только для __фазы 2__.

В нашей системе есть несколько сущностей. Ниже приведено описание основных из них.

## SessionContext

`SessionContext` хранит некоторый набор (мутабельных) свойств, общих на протяжении всего выполнения программы. В нём содержится поле `EnvironmentVariables`, соответствующее текущему состоянию объявленных переменных и `currentDirectory` -- текущая директория.

`SessionContext` является синглтоном (object в котлине).

## Substitutor

`Substitutor` получает на вход пользовательский input в виде строчки, выделяет имена переменных для подстановки, обращается к `SessionContext`'у для получения значений этих переменных и возвращает строчку с подставленными переменными. При этом подстановка не происходит в строки с одинарными кавычками.

API:
- `constructor(EnvironmentVariables)` -- инициализирует `Substitutor` переменными окружения.
- `substitute(String): String` -- выполняет подстановку

## ParserResult
`ParserResult` -- интерфейс для результатов парсинга, следующие классы реализуют данный интерфейс:
### CommandTemplate
`CommandTemplate` -- это дата-класс для представления распаршенной команды. Состоит из имени команды и списка её аргументов в виде строк.
### VariableAssignment
`VariableAssignment` -- дата-класс для представления новых переменных. Состоит из имени новой переменной и ее значения.
### ParseError
`ParseError` -- дата-класс для представления ошибок во время парсинга. Содержит сообщение об ошибке.
### CommandSequenceTemplate
`CommandSequenceTemplate` представляет собой список `CommandTemplate`, идущих подряд.


## CommandParser

`CommandParser` получает на вход строчку, разбивает её на токены и группирует их на `ParserResult`. Для этого используется разбиение по пробелам и символам `|`. На выходе получается список `ParserResult`.

## Command

Интерфейс для команды, состоящий из имени команды и аргументов. Наследники реализуют функцию `execute`. Объект, вызывающий функцию `execute`, обязан знать, откуда читать данные и куда писать результат.

API:
- `execute(inputStream, outputStream, errorStream)`

## CommandBuilder

`CommandBuilder` реализует логику создания `Command` из `CommandTemplate`.

## Pipeline

`Pipeline` представляет собой последовательность `Command`.

## PipelineBuilder

`PipelineBuilder` реализует логику создания `Pipeline` из `CommandSequenceTemplate`.

## CommandExecutor

`CommandExecutor` исполняет переданные команды и управляет потоками ввода-вывода.

API:
- `execute(Pipeline): String` -- вычисляет результат выполнения пайплайна, а именно:
    - поддерживается `curInputStream = System.in` и `curOutputStream = ByteArrayOutputStream()`
    - очередной команде на вход передаётся `curInputStream` и `curOutputStream`
    - затем `curInputStream = curOutputStream.toByteArray().asInputStream()`, `curOutputStream = ByteArrayOutputStream()`, таким образом реализуется пайп
    - в конце `curOutputStream` преобразуется в строчку и возвращается в качестве результата

## Точка входа

В качестве точки входа используется функция main, в которой происходит создание и управление объектами.

## Подробности реализации

- Для разбора аргументов командной строки используется библиотека [kotlin-argparser](https://github.com/xenomachina/kotlin-argparser), как наиболее удобная и простая из существующих. В качестве альтернативы можно было бы использовать [clikt](https://github.com/ajalt/clikt) или [kotlinx-cli](https://github.com/Kotlin/kotlinx-cli).
