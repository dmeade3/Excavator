package graphing;

import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.PieDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.util.HashMap;
import java.util.Map;

public class PieChart extends ApplicationFrame
{

	public PieChart( String title, Map<String, Number> data)
	{
		super(title);
		setContentPane(createPanel(title, data));
		this.setSize(GraphUtil.LINECHART_WINDOW_WIDTH , GraphUtil.LINECHART_WINDOW_HEIGHT);
	}

	private static JFreeChart createChart(String title, PieDataset dataset)
	{
		return ChartFactory.createPieChart(
				title,
				dataset,
				GraphUtil.SHOW_GRAPH_LENGEND,
				GraphUtil.SHOW_GRAPH_TOOLTIP,
				GraphUtil.SHOW_GRAPH_URL
		);
	}

	public static JPanel createPanel(String title, Map<String, Number> data)
	{
		JFreeChart chart = createChart(title, GraphUtil.createPieDataset(data));
		return new ChartPanel(chart);
	}

	public static void main( String[ ] args )
	{
		Map<String, Number> data = new HashMap<>();

		data.put("IPhone 5s" ,     10);
		data.put("SamSung Grand" , 20);
		data.put("MotoG" ,         40);
		data.put("Nokia Lumia" ,   10);

		////////////////////////////////////////////////////////////////////////

		PieChart demo = new PieChart("Mobile Sales", data);

		// TODO Not in the main program, just add these graphs as tabs
		RefineryUtilities.centerFrameOnScreen(demo);

		demo.setVisible( true );
	}
}