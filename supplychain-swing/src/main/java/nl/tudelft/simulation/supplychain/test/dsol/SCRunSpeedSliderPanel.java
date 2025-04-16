package nl.tudelft.simulation.supplychain.test.dsol;

import java.awt.Dimension;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.djutils.swing.multislider.CategorialMultiSlider;

import nl.tudelft.simulation.dsol.simulators.DevsRealTimeAnimator;
import nl.tudelft.simulation.dsol.simulators.DevsSimulatorInterface;

public class SCRunSpeedSliderPanel extends JPanel
{
    /** */
    private static final long serialVersionUID = 20150408L;

    /** The JSlider that the user sees. */
    private final CategorialMultiSlider<Double> slider;

    /** The values at each tick. */
    private Map<Integer, Double> tickValues = new LinkedHashMap<>();

    /**
     * Construct a new TimeWarpPanel.
     * @param minimum double; the minimum value on the scale (the displayed scale may extend a little further than this value)
     * @param maximum double; the maximum value on the scale (the displayed scale may extend a little further than this value)
     * @param initialValue double; the initially selected value on the scale
     * @param ticksPerDecade int; the number of steps per decade
     * @param simulator DevsSimulatorInterface&lt;?, ?, ?&gt;; the simulator to change the speed of
     */
    public SCRunSpeedSliderPanel(final double minimum, final double maximum, final double initialValue, final int ticksPerDecade,
            final DevsSimulatorInterface<?> simulator)
    {
        if (minimum <= 0 || minimum > initialValue || initialValue > maximum)
        {
            throw new RuntimeException("Bad (combination of) minimum, maximum and initialValue; "
                    + "(restrictions: 0 < minimum <= initialValue <= maximum)");
        }
        int[] ratios;
        switch (ticksPerDecade)
        {
            case 1:
                ratios = new int[] {1};
                break;
            case 2:
                ratios = new int[] {1, 3};
                break;
            case 3:
                ratios = new int[] {1, 2, 5};
                break;
            default:
                throw new RuntimeException("Bad ticksPerDecade value (must be 1, 2 or 3)");
        }

        List<Double> scale = List.of(100.0, 200.0, 500.0, 1E3, 2E3, 5E3, 1E4, 2E4, 5E4, 1E5, 2E5, 5E5, 1E6, 1E9);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        for (int i = 0; i < scale.size(); i++)
        {
            String label = "" + scale.get(i).intValue();
            label = label.replace("1000000000", "oo");
            label = label.replace("000000", "M");
            label = label.replace("00000", "00K");
            label = label.replace("0000", "0K");
            label = label.replace("000", "K");
            labels.put(i, new JLabel(label));
            this.tickValues.put(i, scale.get(i));
        }
        this.slider = new CategorialMultiSlider<>(scale, initialValue);
        this.slider.setLabelTable(labels);
        this.slider.setMajorTickSpacing(1);
        this.slider.setMinorTickSpacing(1);
        this.slider.setPaintTicks(true);
        this.slider.setPaintLabels(true);
        this.slider.setPreferredSize(new Dimension(400, 40));
        this.slider.setMinimumSize(new Dimension(400, 40));
        this.slider.setSize(new Dimension(400, 40));
        this.add(this.slider);

        // initial value of simulation speed
        if (simulator instanceof DevsRealTimeAnimator)
        {
            DevsRealTimeAnimator<?> clock = (DevsRealTimeAnimator<?>) simulator;
            clock.setSpeedFactor(this.slider.getValue(0));
        }

        // adjust the simulation speed
        this.slider.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(final ChangeEvent ce)
            {
                @SuppressWarnings("unchecked")
                CategorialMultiSlider<Double> source = (CategorialMultiSlider<Double>) ce.getSource();
                if (simulator instanceof DevsRealTimeAnimator)
                {
                    DevsRealTimeAnimator<?> clock = (DevsRealTimeAnimator<?>) simulator;
                    clock.setSpeedFactor(source.getValue(0));
                }
            }
        });
    }

    /**
     * Access to tickValues map from within the event handler.
     * @return Map&lt;Integer, Double&gt; the tickValues map of this TimeWarpPanel
     */
    protected Map<Integer, Double> getTickValues()
    {
        return this.tickValues;
    }

    /**
     * Retrieve the current TimeWarp factor.
     * @return double; the current TimeWarp factor
     */
    public double getFactor()
    {
        return this.slider.getValue(0);
    }

    @Override
    public String toString()
    {
        return "TimeWarpPanel [timeWarp=" + this.getFactor() + "]";
    }

    /**
     * Set the time warp factor to the best possible approximation of a given value.
     * @param factor double; the requested speed factor
     */
    public void setSpeedFactor(final double factor)
    {
        //
    }
}
