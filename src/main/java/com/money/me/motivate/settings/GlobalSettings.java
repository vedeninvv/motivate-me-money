package com.money.me.motivate.settings;

public interface GlobalSettings {
    double BASIC_TASK_AWARD = 1;
    double INIT_COINS_TASK_MODIFIER = 1;
    double INIT_COINS_PER_HOUR = 0;
    double INIT_BALANCE = 0;
}
//todo Подумать над всеми связями, каскадами и орхан ремувами (Вроде сделано, но можно еще раз проверить)

//todo Подумать над ограничениями для сущностей
//todo В некоторых местах возможно нужно использовать более общие коллекции (вместо List использовать Iterable или что то еще, например)
//todo подумать и поразмышлять над транзакциями