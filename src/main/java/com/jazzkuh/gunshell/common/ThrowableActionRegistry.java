package com.jazzkuh.gunshell.common;

import com.jazzkuh.gunshell.api.enums.BuiltinAmmoActionType;
import com.jazzkuh.gunshell.api.enums.BuiltinThrowableActionType;
import com.jazzkuh.gunshell.api.objects.GunshellThrowable;
import com.jazzkuh.gunshell.common.actions.ammunition.DemoMenuAction;
import com.jazzkuh.gunshell.common.actions.ammunition.EndCreditsAction;
import com.jazzkuh.gunshell.common.actions.throwable.*;
import com.jazzkuh.gunshell.common.actions.throwable.abstraction.AbstractThrowableAction;

import java.util.HashMap;
import java.util.Map;

public class ThrowableActionRegistry {
    private static Map<String, Class<? extends AbstractThrowableAction>> actions = new HashMap<>();

    static {
        actions.put(BuiltinThrowableActionType.EXPLOSIVE.toString(), ExplosiveThrowableAction.class);
        actions.put(BuiltinThrowableActionType.FLASHBANG.toString(), FlashbangThrowableAction.class);
        actions.put(BuiltinThrowableActionType.MOLOTOV.toString(), MolotovThrowableAction.class);
        actions.put(BuiltinThrowableActionType.END_CREDITS.toString(), EndCreditsThrowableAction.class);
        actions.put(BuiltinThrowableActionType.DEMO_MENU.toString(), DemoMenuThrowableAction.class);
    }

    public static void registerAction(String actionType, Class<? extends AbstractThrowableAction> actionClass) {
        actions.remove(actionType);
        actions.put(actionType, actionClass);
    }

    public static void unregisterAction(String actionType) {
        actions.remove(actionType);
    }

    public static AbstractThrowableAction getAction(GunshellThrowable throwable, String actionType) {
        Class<? extends AbstractThrowableAction> actionClass = actions.get(actionType);
        if (actionClass == null) actionClass = ExplosiveThrowableAction.class;

        try {
            return actionClass.getConstructor(GunshellThrowable.class).newInstance(throwable);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
