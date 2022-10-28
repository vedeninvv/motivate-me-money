package com.money.me.motivate.service;

import com.money.me.motivate.domain.modifiers.ModifiersSet;
import org.springframework.stereotype.Service;

@Service
public class ModifiersSetService {
    public void sumModifiersSet(ModifiersSet target, ModifiersSet source) {
        target.setCoinsTaskModifier(
                target.getCoinsTaskModifier() + source.getCoinsTaskModifier()
        );
        target.setCoinsPerHour(
                target.getCoinsPerHour() + source.getCoinsPerHour()
        );
    }

    public ModifiersSet multiplyModifiersSet(ModifiersSet modifiersSet, double multiplier) {
        return new ModifiersSet(
                modifiersSet.getCoinsTaskModifier() * multiplier,
                modifiersSet.getCoinsPerHour() * multiplier
        );
    }
}
