package VASSAL.build.module.map;

import java.util.stream.Stream;

import javax.swing.Icon;

import VASSAL.build.AbstractConfigurable;
import VASSAL.build.AutoConfigurable;
import VASSAL.build.Buildable;
import VASSAL.build.module.Map;
import VASSAL.build.module.documentation.HelpFile;
import VASSAL.configure.StringArrayConfigurer;
import VASSAL.configure.TranslatableStringEnum;
import VASSAL.configure.VisibilityCondition;
import VASSAL.i18n.Resources;
import VASSAL.tools.LaunchButton;
import VASSAL.tools.NamedKeyStroke;

/**
 * Adds a button to the toolbar to manipulate Game Piece Layers
 *
 * @author Brent Easton
 *
 */
public class LayerControl extends AbstractConfigurable {

  public static final String NAME = "name"; //NON-NLS
  public static final String TOOLTIP = "tooltip"; //NON-NLS
  public static final String BUTTON_TEXT = "text"; //NON-NLS
  public static final String BUTTON_ICON = "icon"; //NON-NLS
  public static final String BUTTON_HOTKEY = "hotkey"; //NON-NLS
  public static final String COMMAND = "command"; //NON-NLS
  public static final String SKIP = "skip"; //NON-NLS
  public static final String LAYERS = "layers"; //NON-NLS

  public static final String CMD_ROTATE_UP = "Rotate Layer Order Up"; //NON-NLS - yes, really
  public static final String CMD_ROTATE_DN = "Rotate Layer Order Down"; //NON-NLS - yes, really
  public static final String CMD_ENABLE = "Make Layer Active"; //NON-NLS - yes, really
  public static final String CMD_DISABLE = "Make Layer Inactive"; //NON-NLS - yes, really
  public static final String CMD_TOGGLE = "Switch Layer between Active and Inactive"; //NON-NLS - yes, really
  public static final String CMD_RESET = "Reset All Layers"; //NON-NLS - yes, really

  protected LaunchButton launch;
  protected static final String[] COMMANDS = new String[] {CMD_ROTATE_UP, CMD_ROTATE_DN, CMD_RESET, CMD_ENABLE, CMD_DISABLE, CMD_TOGGLE};
  protected static final String[] COMMANDS_DISPLAY_NAMES = new String[] {
    "Editor.LayerControl.rotate_layer_order_up",
    "Editor.LayerControl.rotate_layer_order_down",
    "Editor.LayerControl.make_layer_active",
    "Editor.LayerControl.make_layer_inactive",
    "Editor.LayerControl.switch_layer_between_active_and_inactive",
    "Editor.LayerControl.reset_all_layers"
  };
  protected String command = CMD_RESET;
  protected boolean skip = true;
  protected String[] layers = new String[0];
  protected LayeredPieceCollection pieceLayers;
  protected CompoundPieceCollection pieceCollection;

  public LayerControl() {
    launch = new LaunchButton("Reset Layers", TOOLTIP, BUTTON_TEXT, BUTTON_HOTKEY, BUTTON_ICON, e -> launch());
  }

  public void launch() {
    if (command.equals(CMD_RESET)) {
      pieceCollection.reset();
    }
    else if (command.equals(CMD_ROTATE_UP)) {
      pieceCollection.rotate(true, skip);
    }
    else if (command.equals(CMD_ROTATE_DN)) {
      pieceCollection.rotate(false, skip);
    }
    else if (command.equals(CMD_ENABLE)) {
      for (int i = 0; i < layers.length; i++) {
        try {
          Integer.parseInt(layers[i]);
          pieceCollection.setLayerEnabled(i, true);
        }
        catch (NumberFormatException e) {
          // User specified a layer name instead of an index
          pieceCollection.setLayerEnabled(layers[i], true);
        }
      }
    }
    else if (command.equals(CMD_DISABLE)) {
      for (int i = 0; i < layers.length; i++) {
        try {
          Integer.parseInt(layers[i]);
          pieceCollection.setLayerEnabled(i, false);
        }
        catch (NumberFormatException e) {
          // User specified a layer name instead of an index
          pieceCollection.setLayerEnabled(layers[i], false);
        }
      }
    }
    else if (command.equals(CMD_TOGGLE)) {
      for (int i = 0; i < layers.length; i++) {
        try {
          Integer.parseInt(layers[i]);
          pieceCollection.toggleLayerEnabled(i);
        }
        catch (NumberFormatException e) {
          // User specified a layer name instead of an index
          pieceCollection.toggleLayerEnabled(layers[i]);
        }
      }
    }
    else {
      return;
    }
    getMap().repaint();
  }

  public LaunchButton getLaunchButton() {
    return launch;
  }

  @Override
  public String[] getAttributeDescriptions() {
    return new String[] {
        Resources.getString(Resources.BUTTON_TEXT),
        Resources.getString(Resources.TOOLTIP_TEXT),
        Resources.getString(Resources.BUTTON_ICON),
        Resources.getString(Resources.HOTKEY_LABEL),
        Resources.getString("Editor.LayerControl.action"), //$NON-NLS-1$
        Resources.getString("Editor.LayerControl.skip_layer"), //$NON-NLS-1$
        Resources.getString("Editor.LayerControl.affect_layer"), //$NON-NLS-1$
    };
  }

  @Override
  public Class<?>[] getAttributeTypes() {
    return new Class<?>[] {
      String.class,
      String.class,
      Icon.class,
      NamedKeyStroke.class,
      CommandConfig.class,
      Boolean.class,
      String[].class
    };
  }

  public static class CommandConfig extends TranslatableStringEnum {
    @Override
    public String[] getValidValues(AutoConfigurable target) {
      return COMMANDS;
    }

    @Override
    public String[] getI18nKeys(AutoConfigurable target) {
      return COMMANDS_DISPLAY_NAMES;
    }
  }

  @Override
  public String[] getAttributeNames() {
    return new String[] {BUTTON_TEXT, TOOLTIP, BUTTON_ICON, BUTTON_HOTKEY, COMMAND, SKIP, LAYERS};
  }

  @Override
  public String getAttributeValueString(String key) {
    if (NAME.equals(key)) {
      return getConfigureName();
    }
    else if (COMMAND.equals(key)) {
      return command;
    }
    else if (SKIP.equals(key)) {
      return String.valueOf(skip);
    }
    else if (LAYERS.equals(key)) {
      return StringArrayConfigurer.arrayToString(layers);
    }
    else  {
      return launch.getAttributeValueString(key);
    }
  }

  @Override
  public void setAttribute(String key, Object value) {
    if (NAME.equals(key)) {
      setConfigureName((String)value);
      if (launch.getAttributeValueString(TOOLTIP) == null) {
        launch.setAttribute(TOOLTIP, value);
      }
    }
    else if (COMMAND.equals(key)) {
      command = (String) value;
    }
    else if (SKIP.equals(key)) {
      if (value instanceof String) {
        value = Boolean.valueOf((String) value);
      }
      skip = (Boolean) value;
    }
    else if (LAYERS.equals(key)) {
      if (value instanceof String) {
        value = StringArrayConfigurer.stringToArray((String) value);
      }
      layers = (String[]) value;
    }
    else {
      launch.setAttribute(key, value);
    }
  }

  @Override
  public VisibilityCondition getAttributeVisibility(String name) {
    if (SKIP.equals(name)) {
      return () -> command.equals(CMD_ROTATE_UP) || command.equals(CMD_ROTATE_DN);
    }
    else if (LAYERS.equals(name)) {
      return () -> Stream.of(CMD_ENABLE, CMD_DISABLE, CMD_TOGGLE).anyMatch(s -> command.equals(s));
    }
    else {
      return null;
    }
  }

  @Override
  public void addTo(Buildable parent) {
    pieceLayers = (LayeredPieceCollection) parent;
    pieceLayers.getToolBar().add(launch);
    pieceCollection = pieceLayers.getPieceCollection();
  }

  public Map getMap() {
    return pieceLayers.getMap();
  }

  @Override
  public Class<?>[] getAllowableConfigureComponents() {
    return new Class<?>[0];
  }

  @Override
  public HelpFile getHelpFile() {
    return HelpFile.getReferenceManualPage("GamePieceLayers.html"); //NON-NLS
  }

  public static String getConfigureTypeName() {
    return Resources.getString("Editor.LayerControl.component_type"); //$NON-NLS-1$
  }

  @Override
  public void removeFrom(Buildable parent) {
    if (getMap() != null) {
      getMap().getToolBar().remove(launch);
    }
  }

}
