package ashlib.data.scripts;

import ashlib.data.plugins.misc.AshMisc;
import ashlib.data.plugins.reflection.ReflectionBetterUtilis;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import com.fs.starfarer.coreui.CaptainPickerDialog;
import com.fs.starfarer.coreui.P;
import org.lwjgl.input.Keyboard;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;

public class AiCoreLevelUpHijacker implements EveryFrameScript {
    private static class ReflectionUtilis {
        // Code taken and modified from Grand Colonies
        private static final Class<?> fieldClass;
        private static final MethodHandles.Lookup lookup = MethodHandles.lookup();
        private static final MethodHandle setFieldHandle;
        private static final MethodHandle getFieldHandle;
        private static final MethodHandle getFieldNameHandle;
        private static final MethodHandle setFieldAccessibleHandle;
        private static final Class<?> methodClass;
        private static final MethodHandle getMethodNameHandle;
        private static final MethodHandle invokeMethodHandle;
        private static final MethodHandle setMethodAccessable;
        private static final MethodHandle getTypeHandle;

        static {
            try {
                fieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
                setFieldHandle = lookup.findVirtual(fieldClass, "set", MethodType.methodType(Void.TYPE, Object.class, Object.class));
                getFieldHandle = lookup.findVirtual(fieldClass, "get", MethodType.methodType(Object.class, Object.class));
                getFieldNameHandle = lookup.findVirtual(fieldClass, "getName", MethodType.methodType(String.class));
                setFieldAccessibleHandle = lookup.findVirtual(fieldClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));
                getTypeHandle = lookup.findVirtual(fieldClass, "getType", MethodType.methodType(Class.class));

                methodClass = Class.forName("java.lang.reflect.Method", false, Class.class.getClassLoader());
                getMethodNameHandle = lookup.findVirtual(methodClass, "getName", MethodType.methodType(String.class));
                invokeMethodHandle = lookup.findVirtual(methodClass, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
                setMethodAccessable = lookup.findVirtual(methodClass, "setAccessible", MethodType.methodType(Void.TYPE, boolean.class));

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        public static List<Object> getAssignableFieldValues(Object instance, Class<?> assignableTo) {
            List<Object> matchingValues = new ArrayList<>();
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                for (Object field : currentClass.getDeclaredFields()) {
                    try {
                        setFieldAccessibleHandle.invoke(field, true);
                        if (assignableTo.isAssignableFrom((Class<?>) getTypeHandle.invoke(field))) {
                            matchingValues.add(getFieldHandle.invoke(field, instance));
                        }
                    } catch (Throwable e) {
                        throw new RuntimeException("Error processing field: " , e);
                    }
                }
                currentClass = currentClass.getSuperclass();
            }

            return matchingValues;
        }
        public static Object getPrivateVariable(String fieldName, Object instanceToGetFrom) {
            try {
                Class<?> instances = instanceToGetFrom.getClass();
                while (instances != null) {
                    for (Object obj : instances.getDeclaredFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    for (Object obj : instances.getFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    instances = instances.getSuperclass();
                }
                return null;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static Object getPrivateVariableFromSuperClass(String fieldName, Object instanceToGetFrom) {
            try {
                Class<?> instances = instanceToGetFrom.getClass();
                while (instances != null) {
                    for (Object obj : instances.getDeclaredFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    for (Object obj : instances.getFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            return getFieldHandle.invoke(obj, instanceToGetFrom);
                        }
                    }
                    instances = instances.getSuperclass();
                }
                return null;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static void setPrivateVariableFromSuperclass(String fieldName, Object instanceToModify, Object newValue) {
            try {
                Class<?> instances = instanceToModify.getClass();
                while (instances != null) {
                    for (Object obj : instances.getDeclaredFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            setFieldHandle.invoke(obj, instanceToModify, newValue);
                            return;
                        }
                    }
                    for (Object obj : instances.getFields()) {
                        setFieldAccessibleHandle.invoke(obj, true);
                        String name = (String) getFieldNameHandle.invoke(obj);
                        if (name.equals(fieldName)) {
                            setFieldHandle.invoke(obj, instanceToModify, newValue);
                            return;
                        }
                    }
                    instances = instances.getSuperclass();
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static boolean hasMethodOfName(String name, Object instance) {
            try {
                for (Object method : instance.getClass().getMethods()) {
                    if (getMethodNameHandle.invoke(method).equals(name)) {
                        return true;
                    }
                }
                return false;
            } catch (Throwable e) {
                return false;
            }
        }

        public static Object invokeMethod(String methodName, Object instance, Object... arguments) {
            try {
                Object method = instance.getClass().getMethod(methodName);
                return invokeMethodHandle.invoke(method, instance, arguments);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        public static List<UIComponentAPI> getChildrenCopy(UIPanelAPI panel) {
            try {
                return (List<UIComponentAPI>) invokeMethod("getChildrenCopy", panel);
            } catch (Throwable e) {
                return null;
            }
        }

        public static Pair<Object, Class<?>[]> getMethodFromSuperclass(String methodName, Object instance) {
            Class<?> currentClass = instance.getClass();

            while (currentClass != null) {
                // Retrieve all declared methods in the current class
                Object[] methods = currentClass.getDeclaredMethods();

                for (Object method : methods) {
                    try {
                        // Retrieve the MethodHandle for the getParameterTypes method
                        MethodHandle getParameterTypesHandle = ReflectionBetterUtilis.getParameterTypesHandle(method.getClass(), "getParameterTypes");
                        // Use the MethodHandle to retrieve the method's name

                        // Check if the method name matches
                        if (getMethodNameHandle.invoke(method).equals(methodName)) {
                            // Invoke the MethodHandle to get the parameter types
                            Class<?>[] parameterTypes = (Class<?>[]) getParameterTypesHandle.invoke(method);
                            return new Pair<>(method, parameterTypes);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();  // Handle any reflection errors
                    }
                }
                // Move to the superclass if no match is found
                currentClass = currentClass.getSuperclass();
            }

            // Return null if the method was not found in the class hierarchy
            return null;
        }

        public static Object invokeMethodWithAutoProjection(String methodName, Object instance, Object... arguments) {
            // Retrieve the method and its parameter types
            Pair<Object, Class<?>[]> methodPair = getMethodFromSuperclass(methodName, instance);

            // Check if the method was found
            if (methodPair == null) {
                try {
                    throw new NoSuchMethodException("Method " + methodName + " not found in class hierarchy of " + instance.getClass().getName());
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }

            Object method = methodPair.one;
            Class<?>[] parameterTypes = methodPair.two;

            // Prepare arguments by projecting them to the correct types
            Object[] projectedArgs = new Object[parameterTypes.length];
            for (int index = 0; index < parameterTypes.length; index++) {
                Object arg = (arguments.length > index) ? arguments[index] : null;

                if (arg == null) {
                    // If the expected type is a primitive type, throw an exception
                    if (parameterTypes[index].isPrimitive()) {
                        throw new IllegalArgumentException("Argument at index " + index + " cannot be null for primitive type " + parameterTypes[index].getName());
                    }
                    projectedArgs[index] = null; // Keep nulls as null for reference types
                } else {
                    // Try to convert the argument to the expected parameter type
                    try {
                        projectedArgs[index] = convertArgument(arg, parameterTypes[index]);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Cannot convert argument at index " + index + " to " + parameterTypes[index].getName(), e);
                    }
                }
            }

            // Ensure the method is accessible
            try {
                setMethodAccessable.invoke(method, true);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            // Invoke the method with the projected arguments
            try {
                return invokeMethodHandle.invoke(method, instance, projectedArgs);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        // Helper function to convert an argument to the expected type
        public static Object convertArgument(Object arg, Class<?> targetType) {
            if (targetType.isAssignableFrom(arg.getClass())) {
                return arg; // Use as-is if types match
            } else if (targetType.isPrimitive()) {
                // Handle primitive types by boxing
                if (targetType == int.class) {
                    return ((Number) arg).intValue();
                } else if (targetType == long.class) {
                    return ((Number) arg).longValue();
                } else if (targetType == double.class) {
                    return ((Number) arg).doubleValue();
                } else if (targetType == float.class) {
                    return ((Number) arg).floatValue();
                } else if (targetType == short.class) {
                    return ((Number) arg).shortValue();
                } else if (targetType == byte.class) {
                    return ((Number) arg).byteValue();
                } else if (targetType == boolean.class) {
                    return arg;
                } else if (targetType == char.class) {
                    return arg;
                } else {
                    throw new IllegalArgumentException("Unsupported primitive type: " + targetType.getName());
                }
            } else {
                // For reference types, perform a cast if possible
                return targetType.cast(arg);
            }
        }
    }

    public static List<UIComponentAPI> getChildren(UIPanelAPI panelAPI) {
        return ReflectionUtilis.getChildrenCopy(panelAPI);
    }

    private static class ProductionUtil {
        public static UIPanelAPI getCoreUI() {
            CampaignUIAPI campaignUI;
            campaignUI = Global.getSector().getCampaignUI();
            InteractionDialogAPI dialog = campaignUI.getCurrentInteractionDialog();

            CoreUIAPI core;
            if (dialog == null) {
                core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCore", campaignUI);
            } else {
                core = (CoreUIAPI) ReflectionUtilis.invokeMethod("getCoreUI", dialog);
            }
            return core == null ? null : (UIPanelAPI) core;
        }

        public static UIPanelAPI getCurrentTab() {
            UIPanelAPI coreUltimate = getCoreUI();
            UIPanelAPI core = (UIPanelAPI) ReflectionUtilis.invokeMethod("getCurrentTab", coreUltimate);
            return core == null ? null : (UIPanelAPI) core;
        }
    }
    transient Object officerRowData;
    transient ButtonAPI button;
    transient Object currDialog;
    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return true;
    }

    @Override
    public void advance(float amount) {
        if(Global.getSector().getCampaignUI().getCurrentCoreTab()!=null) {
            if(button!=null&&button.isChecked()){
                button.setChecked(false);
                PersonAPI person = (PersonAPI) ReflectionUtilis.invokeMethod("getPerson",officerRowData);
                Misc.setUnremovable(person,true);
                person.getStats().setLevel(person.getStats().getLevel()+1);
                ReflectionUtilis.invokeMethod("recreate",officerRowData);
                officerRowData = null;
                button = null;
                ReflectionUtilis.invokeMethodWithAutoProjection("dismiss",currDialog,1);
                Global.getSoundPlayer().playUISound("ui_char_spent_story_point_technology",1f,1f);
                currDialog = null;
            }
            Object skillDialog = getSkillDialog();
            if(skillDialog!=null){
                if(officerRowData==null){
                    Object listOfficers =ReflectionUtilis.invokeMethod("getListOfficers", skillDialog);
                    ArrayList<Object>listItems = (ArrayList<Object>) ReflectionUtilis.invokeMethod("getItems",listOfficers);
                    for (Object listItem : listItems) {
                        for (Object assignableFieldValue : ReflectionUtilis.getAssignableFieldValues(listItem,ButtonAPI.class)) {
                            if(assignableFieldValue instanceof ButtonAPI){
                                ButtonAPI button = (ButtonAPI) assignableFieldValue;
                                if(AshMisc.isStringValid(button.getText())){
                                    if(button.getText().contains("integrate")){
                                        if(button.isChecked()){
                                            officerRowData = listItem;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                List<UIComponentAPI> comps = ReflectionUtilis.getChildrenCopy(ProductionUtil.getCoreUI());

                if(comps.size()>=19 &&button==null){
                    UIComponentAPI comp = comps.get(comps.size()-1);
                    UIPanelAPI compPanel = (UIPanelAPI) ReflectionUtilis.invokeMethod("getInnerPanel",comp);
                        boolean found = officerRowData!=null;
                        if(found){
                            for (UIComponentAPI componentAPI : ReflectionUtilis.getChildrenCopy(compPanel)) {
                                if(componentAPI instanceof ButtonAPI){
                                    if(((ButtonAPI) componentAPI).getText() != null && ((ButtonAPI) componentAPI).getText().toLowerCase().contains("confirm")){
                                        CustomPanelAPI panel = Global.getSettings().createCustom(componentAPI.getPosition().getWidth(),componentAPI.getPosition().getHeight(),null);
                                        TooltipMakerAPI tooltip = panel.createUIElement(panel.getPosition().getWidth(),panel.getPosition().getHeight(),false);
                                        tooltip.setButtonFontOrbitron20();
                                        button =tooltip.addButton("Confirm",null,Misc.getStoryOptionColor(),Misc.getStoryDarkColor(),Alignment.MID,CutStyle.TL_BR,panel.getPosition().getWidth(),panel.getPosition().getHeight(),0f);
                                        button.setShortcut(Keyboard.KEY_G,true);
                                        button.setEnabled(((ButtonAPI) componentAPI).isEnabled()
                                        );
                                        panel.addUIElement(tooltip).inTL(0,0);
                                        compPanel.addComponent(panel).inTL(componentAPI.getPosition().getX()-compPanel.getPosition().getX(),compPanel.getPosition().getY()-componentAPI.getPosition().getY()+compPanel.getPosition().getHeight()-componentAPI.getPosition().getHeight());
                                        compPanel.removeComponent(componentAPI);
                                        currDialog = comp;
                                        break;
                                    }

                                }
                            }
                        }



                }
                else if(comps.size()<19) {
                    button =  null;
                    currDialog = null;
                    officerRowData = null;
                }

            }
            else {
                button =  null;
                currDialog = null;
                officerRowData = null;
            }
        }
        else {
            button =  null;
            currDialog = null;
            officerRowData = null;
        }
    }
    public Object  getSkillDialog(){
        try {
            for (UIComponentAPI child : getChildren(ProductionUtil.getCoreUI())) {
                if(child instanceof CaptainPickerDialog){
                    return child;
                }
            }
        }
        catch (Exception e ){
            return null;
        }

        return null;
    }
}
