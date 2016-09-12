package mcjty.rftoolscontrol.logic.editors;

import mcjty.lib.gui.layout.HorizontalAlignment;
import mcjty.lib.gui.layout.HorizontalLayout;
import mcjty.lib.gui.layout.PositionalLayout;
import mcjty.lib.gui.widgets.*;
import mcjty.rftoolscontrol.logic.Parameter;
import mcjty.rftoolscontrol.logic.registry.Function;
import mcjty.rftoolscontrol.logic.registry.Functions;
import mcjty.rftoolscontrol.logic.registry.ParameterType;
import mcjty.rftoolscontrol.logic.registry.ParameterValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractParameterEditor implements ParameterEditor {

    public static final String PAGE_CONSTANT = "Constant";
    public static final String PAGE_VARIABLE = "Variable";
    public static final String PAGE_FUNCTION = "Function";

    private TextField variableIndex;
    private TabbedPanel tabbedPanel;
    private Panel buttonPanel;
    private ChoiceLabel functionLabel;

    public static Integer parseIntSafe(String newText) {
        Integer f;
        try {
            f = Integer.parseInt(newText);
        } catch (NumberFormatException e) {
            f = null;
        }
        return f;
    }

    protected abstract ParameterValue readConstantValue();

    protected abstract void writeConstantValue(ParameterValue value);

    @Override
    public ParameterValue readValue() {
        if (PAGE_CONSTANT.equals(tabbedPanel.getCurrentName())) {
            return readConstantValue();
        } else if (PAGE_VARIABLE.equals(tabbedPanel.getCurrentName())) {
            Integer var = parseIntSafe(variableIndex.getText());
            if (var != null) {
                return ParameterValue.variable(var);
            } else {
                return ParameterValue.variable(0);
            }
        } else if (PAGE_FUNCTION.equals(tabbedPanel.getCurrentName())) {
            List<Parameter> parameters = new ArrayList<>();
            // @todo fill the list of parameters from the gui
            String currentChoice = functionLabel.getCurrentChoice();
            return ParameterValue.function(Functions.FUNCTIONS.get(currentChoice), parameters);
        }
        return null;
    }

    @Override
    public void writeValue(ParameterValue value) {
        if (value == null || value.isConstant()) {
            switchPage(PAGE_CONSTANT, null);
            writeConstantValue(value);
        } else if (value.isVariable()) {
            switchPage(PAGE_VARIABLE, null);
            variableIndex.setText(Integer.toString(value.getVariableIndex()));
        } else if (value.isFunction()) {
            switchPage(PAGE_FUNCTION, null);
            String id = value.getFunction().getId();
            functionLabel.setChoice(id);
        }
    }

    protected Panel createLabeledPanel(Minecraft mc, Gui gui, String label, Widget object, String... tooltips) {
        object.setTooltips(tooltips);
        return new Panel(mc, gui).setLayout(new HorizontalLayout())
                .addChild(new Label(mc, gui)
                        .setHorizontalAlignment(HorizontalAlignment.ALIGH_LEFT)
                        .setText(label)
                        .setTooltips(tooltips)
                        .setDesiredWidth(60))
                .addChild(object);
    }

    void createEditorPanel(Minecraft mc, Gui gui, Panel panel, ParameterEditorCallback callback, Panel constantPanel,
                           ParameterType type) {
        Panel variablePanel = new Panel(mc, gui).setLayout(new HorizontalLayout()).setDesiredHeight(18);
        variableIndex = new TextField(mc, gui)
                .setDesiredHeight(14)
                .setTooltips("Index (in the processor)", "of the variable")
                .addTextEvent((parent,newText) -> callback.valueChanged(readValue()));
        variablePanel.addChild(new Label(mc, gui)
                .setText("Index:"))
                .setTooltips("Index (in the processor)", "of the variable")
                .setDesiredHeight(14)
                .addChild(variableIndex);

        Panel functionPanel = new Panel(mc, gui).setLayout(new HorizontalLayout());
        functionLabel = new ChoiceLabel(mc, gui)
                .setDesiredWidth(70);
        List<Function> functions = Functions.getFunctionsByType(type);
        for (Function function : functions) {
            functionLabel.addChoices(function.getId());
        }
        functionPanel.addChild(functionLabel);
        functionLabel.addChoiceEvent(((parent, newChoice) -> callback.valueChanged(readValue())));

        tabbedPanel = new TabbedPanel(mc, gui)
                .addPage(PAGE_CONSTANT, constantPanel)
                .addPage(PAGE_VARIABLE, variablePanel)
                .addPage(PAGE_FUNCTION, functionPanel);
        tabbedPanel.setLayoutHint(new PositionalLayout.PositionalHint(5, 5 + 18, 190-10, 60 + getHeight() -5-18 -40));


        buttonPanel = new Panel(mc, gui).setLayout(new HorizontalLayout())
            .setLayoutHint(new PositionalLayout.PositionalHint(5, 5, 190-10, 18));
        ToggleButton constantButton = new ToggleButton(mc, gui).setText(PAGE_CONSTANT)
                .addButtonEvent(w -> switchPage(PAGE_CONSTANT, callback));
        ToggleButton variableButton = new ToggleButton(mc, gui).setText(PAGE_VARIABLE)
                .addButtonEvent(w -> switchPage(PAGE_VARIABLE, callback));
        ToggleButton functionButton = new ToggleButton(mc, gui).setText(PAGE_FUNCTION)
                .addButtonEvent(w -> switchPage(PAGE_FUNCTION, callback));
        buttonPanel.addChild(constantButton).addChild(variableButton).addChild(functionButton);

        panel.addChild(buttonPanel).addChild(tabbedPanel);
    }

    private void switchPage(String page, ParameterEditorCallback callback) {
        for (int i = 0 ; i < buttonPanel.getChildCount() ; i++) {
            ToggleButton button = (ToggleButton) buttonPanel.getChild(i);
            if (!page.equals(button.getText())) {
                button.setPressed(false);
            } else {
                button.setPressed(true);
            }
            tabbedPanel.setCurrent(page);
            if (callback != null) {
                callback.valueChanged(readValue());
            }
        }

    }
}
