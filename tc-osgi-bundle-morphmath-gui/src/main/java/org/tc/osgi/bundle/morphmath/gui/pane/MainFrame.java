package org.tc.osgi.bundle.morphmath.gui.pane;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.osgi.framework.BundleContext;
import org.tc.osgi.bundle.gui.utils.pane.ImagePane;
import org.tc.osgi.bundle.morphmath.core.exception.MorphologiqueException;
import org.tc.osgi.bundle.morphmath.gui.conf.MorphMathGuiPropertyFile;
import org.tc.osgi.bundle.morphmath.gui.module.activator.MorphMathGuiActivator;
import org.tc.osgi.bundle.morphmath.gui.module.service.GuiUtilsServiceProxy;
import org.tc.osgi.bundle.morphmath.gui.module.service.MorphMathCoreServiceProxy;
import org.tc.osgi.bundle.morphmath.gui.module.service.UtilsServiceProxy;
import org.tc.osgi.bundle.utils.exception.FieldTrackingAssignementException;

/**
 * MainFrame.java.
 * @author collonville thomas
 * @version
 * @track
 */
public class MainFrame extends JFrame {

    /**
     * long serialVersionUID.
     */
    private static final long serialVersionUID = -4857310553881121356L;

    /**
     * String app_title.
     */
    private final String app_title = null;

    /**
     * JMenuBar menuBar.
     */
    private JMenuBar menuBar;

    /**
     * MainFrame constructor.
     * @param context BundleContext
     * @throws FieldTrackingAssignementException
     */
    public MainFrame(final BundleContext context) throws FieldTrackingAssignementException {
        UtilsServiceProxy.getInstance().getLogger(MainFrame.class).debug("Construction du mainframe");
        setTitle(getAppTitle());
        addWindowListener(GuiUtilsServiceProxy.getInstance().getBundleClosingWindowsAdapter(context, MorphMathGuiActivator.AUTO_BUNDLE_NAME));
        buildComponent();
        pack();
        setVisible(true);
    }

    /**
     * buildComponent.
     */
    private void buildComponent() {
        buildMenu();

    }

    /**
     * buildMenu.
     */
    private void buildMenu() {
        menuBar = new JMenuBar();

        final JMenuItem itemGo = new JMenuItem(MorphMathGuiPropertyFile.getInstance().getActionGo());
        itemGo.setName(MorphMathGuiPropertyFile.getInstance().getActionGo());
        menuBar.add(itemGo);

        final JMenuItem menuSelection = new JMenuItem(MorphMathGuiPropertyFile.getInstance().getActionSelect());
        menuSelection.setName(MorphMathGuiPropertyFile.getInstance().getActionSelect());
        menuBar.add(menuSelection);
        final JComboBox<String> combo = new JComboBox<>(new String[] { MorphMathGuiPropertyFile.getInstance().getDilatation(), MorphMathGuiPropertyFile.getInstance().getErosion(),
            MorphMathGuiPropertyFile.getInstance().getClose(), MorphMathGuiPropertyFile.getInstance().getGradient(), MorphMathGuiPropertyFile.getInstance().getOpen() });
        combo.setName("combo1");
        menuSelection.add(combo);

        itemGo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent e) {
                final String comboResult = combo.getSelectedItem().toString();
                UtilsServiceProxy.getInstance().getLogger(MainFrame.class).debug(comboResult);

                final JFileChooser chooser = new JFileChooser();
                chooser.setToolTipText(combo.getSelectedItem().toString());
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                final int returnVal = chooser.showOpenDialog(chooser);
                final File file = chooser.getSelectedFile();
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    UtilsServiceProxy.getInstance().getLogger(this.getClass()).debug("File selected by jfilechooser");
                    UtilsServiceProxy.getInstance().getLogger(MainFrame.class).debug(file.getAbsolutePath());
                    File output = null;
                    try {
                        final JDialog dial = new JDialog();
                        final ImagePane panel = new ImagePane(ImageIO.read(file));
                        dial.getContentPane().add(panel);
                        dial.setVisible(true);
                        dial.pack();

                        if (comboResult.endsWith(MorphMathGuiPropertyFile.getInstance().getDilatation())) {
                            output = MorphMathCoreServiceProxy.getInstance().dilatation(file);
                        }

                        if (comboResult.endsWith(MorphMathGuiPropertyFile.getInstance().getErosion())) {
                            output = MorphMathCoreServiceProxy.getInstance().erosion(file);
                        }

                        if (comboResult.endsWith(MorphMathGuiPropertyFile.getInstance().getClose())) {
                            output = MorphMathCoreServiceProxy.getInstance().close(file);
                        }
                        if (comboResult.endsWith(MorphMathGuiPropertyFile.getInstance().getGradient())) {
                            output = MorphMathCoreServiceProxy.getInstance().gradient(file);
                        }
                        if (comboResult.endsWith(MorphMathGuiPropertyFile.getInstance().getOpen())) {
                            output = MorphMathCoreServiceProxy.getInstance().open(file);
                        }

                        if (output != null) {

                            final JDialog dialout = new JDialog();
                            UtilsServiceProxy.getInstance().getLogger(this.getClass()).debug("Output file generated");
                            UtilsServiceProxy.getInstance().getLogger(MainFrame.class).debug(output.getAbsolutePath());
                            final ImagePane panelout = new ImagePane(ImageIO.read(output));
                            // TODO utiliser les service gui-utils
                            dialout.getContentPane().add(panelout);
                            dialout.setVisible(true);
                            dialout.pack();

                        }
                    } catch (IOException | MorphologiqueException e1) {
                        UtilsServiceProxy.getInstance().getLogger(MainFrame.class).error(e1.getMessage(), e1);
                        final JDialog d = new JDialog(MainFrame.this, MorphMathGuiPropertyFile.getInstance().getError());
                        d.add(new JLabel(e1.getMessage()));
                        // TODO utiliser les service gui-utils
                        d.setVisible(true);
                        d.pack();

                    } catch (final FieldTrackingAssignementException e1) {
                        UtilsServiceProxy.getInstance().getLogger(MainFrame.class).error(e1.getMessage(), e1);
                        final JDialog d = new JDialog(MainFrame.this, MorphMathGuiPropertyFile.getInstance().getError());
                        d.add(new JLabel(e1.getMessage()));
                        // TODO utiliser les service gui-utils
                        d.setVisible(true);
                        d.pack();
                    }

                }
                if (returnVal == JFileChooser.CANCEL_OPTION) {
                    UtilsServiceProxy.getInstance().getLogger(this.getClass()).debug("Cancel action jfilechooser");
                }
            }
        });

        setJMenuBar(menuBar);
    }

    /**
     * getAppTitle.
     * @return IUtilsService
     * @throws FieldTrackingAssignementException
     */
    public String getAppTitle() throws FieldTrackingAssignementException {
        if (app_title == null) {
            UtilsServiceProxy.getInstance().getXMLPropertyFile(MorphMathGuiPropertyFile.getInstance().getXMLFile()).fieldTraking(this, "app_title");
        }
        return app_title;
    }
}
