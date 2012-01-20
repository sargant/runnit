package com.sargant.runnit.gui;

import java.io.IOException;
import java.text.DecimalFormat;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

import com.sargant.runnit.GPSPoint;
import com.sargant.runnit.GPSTrack;
import com.sargant.runnit.RunnitApp;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.xml.sax.SAXException;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.browser.Browser;

public class MainWindow {
	
	private String dateFormat = "EEE d MMM yyyy, HH:mm:ss"; 

	private RunnitApp app;
	protected Shell shlRunnit;
	private Text textFilename;
	private TabFolder tabFolder;
	private TabItem tbtmOverview;
	private Composite overviewComposite;
	private SashForm sashForm;
	private Composite composite;
	private TabItem tbtmLogs;
	private Composite composite_1;
	private Table tablePoints;
	private TableColumn tblclmnLatitude;
	private TableColumn tblclmnLongitude;
	private TableColumn tblclmnElevation;
	private TableColumn tblclmnTime;
	private TableColumn tblclmnNewColumn;
	private Text textTimeStarted;
	private Label lblTimeStarted;
	private Text textTimeDuration;
	private Label lblDuration;
	
	private DecimalFormat longDistanceFormatter;
	private DecimalFormat shortDistanceFormatter;
	private DecimalFormat degreesFormatter;
	private Text textTotalDistance;
	private Label lblDistance;
	private Text textAverageSpeed;
	private Label lblAverageSpeed;
	private Text textAveragePace;
	private Label lblAveragePace;
	private Text textTotalClimb;
	private Label lblClimb;
	private TabItem tbtmMap;
	private Composite composite_2;
	private Table table;
	private Browser browser;
	private Combo comboMapType;
	private TableColumn tblclmnSpeed;

	public MainWindow (RunnitApp a) {
		app = a;
	}
	
	/**
	 * Open the window.
	 * @wbp.parser.entryPoint
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		
		Monitor primary = display.getPrimaryMonitor ();
		Rectangle bounds = primary.getBounds ();
		Rectangle rect = shlRunnit.getBounds ();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		shlRunnit.setLocation (x, y);
		
		shlRunnit.open();
		shlRunnit.layout();
		
		while (!shlRunnit.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 * @param main 
	 * @wbp.parser.entryPoint
	 */
	public void createContents() {
		
		longDistanceFormatter = new DecimalFormat();
		longDistanceFormatter.setMaximumFractionDigits(2);
		longDistanceFormatter.setMinimumFractionDigits(2);
		longDistanceFormatter.setPositiveSuffix(" km");
		longDistanceFormatter.setNegativeSuffix(" km");
		
		shortDistanceFormatter = new DecimalFormat();
		shortDistanceFormatter.setMaximumFractionDigits(1);
		shortDistanceFormatter.setMinimumFractionDigits(1);
		shortDistanceFormatter.setPositiveSuffix(" m");
		shortDistanceFormatter.setNegativeSuffix(" m");
		
		degreesFormatter = new DecimalFormat();
		degreesFormatter.setMaximumFractionDigits(6);
		degreesFormatter.setMinimumFractionDigits(6);
		degreesFormatter.setPositivePrefix("+");
		degreesFormatter.setNegativePrefix("-");
		
		shlRunnit = new Shell(SWT.DIALOG_TRIM | SWT.MIN);
		shlRunnit.setImages(new Image[] {
				SWTResourceManager.getImage(this.getClass(), "/res/running_16.png"),
				SWTResourceManager.getImage(this.getClass(), "/res/running_22.png"),
				SWTResourceManager.getImage(this.getClass(), "/res/running_32.png"),
				SWTResourceManager.getImage(this.getClass(), "/res/running_48.png"),
		});
		shlRunnit.setSize(800,640);
		shlRunnit.setText("Runnit");
		shlRunnit.setLayout(new GridLayout(2, false));
		
		Button btnSelectAGpx = new Button(shlRunnit, SWT.NONE);
		btnSelectAGpx.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnSelectAGpx.setText("Select a GPX file");
		btnSelectAGpx.addSelectionListener(new SelectionAdapter() {
			
			public void widgetSelected(SelectionEvent e) {
				
				FileDialog fd = new FileDialog(shlRunnit);
				fd.setFilterNames(new String[] {"GPX files (*.gpx)", "All files"});
				fd.setFilterExtensions(new String[] {"*.gpx", "*"});
				String file = fd.open();
				
				if(file != null) {
					resetUI();
					getTextFilename().setText(file);
				}
			}
		});
		
		textFilename = new Text(shlRunnit, SWT.BORDER | SWT.READ_ONLY);
		textFilename.addModifyListener(new ModifyListener() {			
			public void modifyText(ModifyEvent e) {
				
				try {
					resetUI();
					app.setFilename(getTextFilename().getText());
					newFileLoaded();
					
				} catch(IOException e1) {
					MessageDialog.openError(shlRunnit, getTextFilename().getText(), "Error opening file.");
				} catch(SAXException e1) {
					MessageDialog.openError(shlRunnit, getTextFilename().getText(), "Could not parse file. Are you sure it is a GPX file?");
				}
			}
		});
		
		textFilename.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		tabFolder = new TabFolder(shlRunnit, SWT.NONE);
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 2, 1));
		tabFolder.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {checkTabFocus();}
			public void widgetDefaultSelected(SelectionEvent e) {checkTabFocus();}
		});
		
		tbtmOverview = new TabItem(tabFolder, SWT.NONE);
		tbtmOverview.setText("Overview");
		
		sashForm = new SashForm(tabFolder, SWT.NONE);
		sashForm.setSashWidth(20);
		tbtmOverview.setControl(sashForm);
		
		overviewComposite = new Composite(sashForm, SWT.NONE);
		overviewComposite.setLayout(new FormLayout());
		
		textTimeStarted = new Text(overviewComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		FormData fd_textTimeStarted = new FormData();
		fd_textTimeStarted.top = new FormAttachment(0, 10);
		fd_textTimeStarted.left = new FormAttachment(100, -260);
		textTimeStarted.setLayoutData(fd_textTimeStarted);
		
		lblTimeStarted = new Label(overviewComposite, SWT.NONE);
		FormData fd_lblTimeStarted = new FormData();
		fd_lblTimeStarted.top = new FormAttachment(0, 13);
		fd_lblTimeStarted.left = new FormAttachment(0, 10);
		lblTimeStarted.setLayoutData(fd_lblTimeStarted);
		lblTimeStarted.setText("Started");
		fd_textTimeStarted.right = new FormAttachment(100, -10);
		
		lblDuration = new Label(overviewComposite, SWT.NONE);
		lblDuration.setText("Duration");
		FormData fd_lblDuration = new FormData();
		fd_lblDuration.top = new FormAttachment(lblTimeStarted, 12);
		fd_lblDuration.left = new FormAttachment(lblTimeStarted, 0, SWT.LEFT);
		lblDuration.setLayoutData(fd_lblDuration);
		
		textTotalDistance = new Text(overviewComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		FormData fd_textTotalDistance = new FormData();
		fd_textTotalDistance.right = new FormAttachment(100, -10);
		textTotalDistance.setLayoutData(fd_textTotalDistance);
		
		lblDistance = new Label(overviewComposite, SWT.NONE);
		lblDistance.setText("Distance");
		FormData fd_lblDistance = new FormData();
		fd_lblDistance.top = new FormAttachment(lblDuration, 12);
		fd_lblDistance.left = new FormAttachment(lblTimeStarted, 0, SWT.LEFT);
		lblDistance.setLayoutData(fd_lblDistance);
		
		Label label = new Label(overviewComposite, SWT.SEPARATOR | SWT.HORIZONTAL);
		FormData fd_label = new FormData();
		fd_label.top = new FormAttachment(textTotalDistance, 6);
		fd_label.left = new FormAttachment(0, 10);
		fd_label.right = new FormAttachment(100, -10);
		label.setLayoutData(fd_label);
		
		textTimeDuration = new Text(overviewComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		fd_textTotalDistance.left = new FormAttachment(textTimeDuration, 0, SWT.LEFT);
		fd_textTotalDistance.top = new FormAttachment(textTimeDuration, 6);
		FormData fd_textTimeDuration = new FormData();
		fd_textTimeDuration.top = new FormAttachment(textTimeStarted, 6);
		fd_textTimeDuration.left = new FormAttachment(lblDuration, 162);
		fd_textTimeDuration.right = new FormAttachment(100, -10);
		textTimeDuration.setLayoutData(fd_textTimeDuration);
		
		textAverageSpeed = new Text(overviewComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		fd_label.bottom = new FormAttachment(textAverageSpeed, -6);
		FormData fd_textAverageSpeed = new FormData();
		fd_textAverageSpeed.left = new FormAttachment(textTotalDistance, 0, SWT.LEFT);
		fd_textAverageSpeed.right = new FormAttachment(100, -10);
		fd_textAverageSpeed.top = new FormAttachment(0, 99);
		textAverageSpeed.setLayoutData(fd_textAverageSpeed);
		
		lblAverageSpeed = new Label(overviewComposite, SWT.NONE);
		lblAverageSpeed.setText("Average speed");
		FormData fd_lblAverageSpeed = new FormData();
		fd_lblAverageSpeed.top = new FormAttachment(label, 9);
		fd_lblAverageSpeed.left = new FormAttachment(lblTimeStarted, 0, SWT.LEFT);
		lblAverageSpeed.setLayoutData(fd_lblAverageSpeed);
		
		textAveragePace = new Text(overviewComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		FormData fd_textAveragePace = new FormData();
		fd_textAveragePace.left = new FormAttachment(textTotalDistance, 0, SWT.LEFT);
		fd_textAveragePace.top = new FormAttachment(textAverageSpeed, 6);
		fd_textAveragePace.right = new FormAttachment(100, -10);
		textAveragePace.setLayoutData(fd_textAveragePace);
		
		lblAveragePace = new Label(overviewComposite, SWT.NONE);
		lblAveragePace.setText("Average pace");
		FormData fd_lblAveragePace = new FormData();
		fd_lblAveragePace.top = new FormAttachment(lblAverageSpeed, 12);
		fd_lblAveragePace.left = new FormAttachment(lblTimeStarted, 0, SWT.LEFT);
		lblAveragePace.setLayoutData(fd_lblAveragePace);
		
		textTotalClimb = new Text(overviewComposite, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		FormData fd_textTotalClimb = new FormData();
		fd_textTotalClimb.left = new FormAttachment(textTotalDistance, 0, SWT.LEFT);
		fd_textTotalClimb.top = new FormAttachment(textAveragePace, 6);
		fd_textTotalClimb.right = new FormAttachment(100, -10);
		textTotalClimb.setLayoutData(fd_textTotalClimb);
		
		lblClimb = new Label(overviewComposite, SWT.NONE);
		lblClimb.setText("Climb");
		FormData fd_lblClimb = new FormData();
		fd_lblClimb.top = new FormAttachment(lblAveragePace, 12);
		fd_lblClimb.left = new FormAttachment(lblTimeStarted, 0, SWT.LEFT);
		lblClimb.setLayoutData(fd_lblClimb);
		
		composite = new Composite(sashForm, SWT.NONE);
		composite.setLayout(new FormLayout());
		
		table = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
		FormData fd_table = new FormData();
		fd_table.left = new FormAttachment(0, 10);
		fd_table.bottom = new FormAttachment(0, 534);
		fd_table.right = new FormAttachment(0, 368);
		table.setLayoutData(fd_table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnLap = new TableColumn(table, SWT.RIGHT);
		tblclmnLap.setWidth(50);
		tblclmnLap.setText("Km");
		
		TableColumn tblclmnNewColumn_1 = new TableColumn(table, SWT.RIGHT);
		tblclmnNewColumn_1.setWidth(80);
		tblclmnNewColumn_1.setText("Time");
		
		TableColumn tblclmnNewColumn_2 = new TableColumn(table, SWT.RIGHT);
		tblclmnNewColumn_2.setWidth(100);
		tblclmnNewColumn_2.setText("Pace");
		
		Label lblKilometerSplits = new Label(composite, SWT.NONE);
		fd_table.top = new FormAttachment(lblKilometerSplits, 6);
		FormData fd_lblKilometerSplits = new FormData();
		fd_lblKilometerSplits.top = new FormAttachment(0, 10);
		fd_lblKilometerSplits.left = new FormAttachment(0, 10);
		lblKilometerSplits.setLayoutData(fd_lblKilometerSplits);
		lblKilometerSplits.setText("Kilometer Splits");
		sashForm.setWeights(new int[] {1, 1});
		
		tbtmLogs = new TabItem(tabFolder, SWT.NONE);
		tbtmLogs.setText("Logs");
		
		composite_1 = new Composite(tabFolder, SWT.NONE);
		tbtmLogs.setControl(composite_1);
		
		tablePoints = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
		tablePoints.setHeaderVisible(true);
		tablePoints.setBounds(10, 10, 756, 524);
		
		tblclmnTime = new TableColumn(tablePoints, SWT.RIGHT);
		tblclmnTime.setWidth(200);
		tblclmnTime.setText("Time");
		
		tblclmnLatitude = new TableColumn(tablePoints, SWT.RIGHT);
		tblclmnLatitude.setWidth(120);
		tblclmnLatitude.setText("Latitude");
		
		tblclmnLongitude = new TableColumn(tablePoints, SWT.RIGHT);
		tblclmnLongitude.setWidth(120);
		tblclmnLongitude.setText("Longitude");
		
		tblclmnElevation = new TableColumn(tablePoints, SWT.RIGHT);
		tblclmnElevation.setWidth(80);
		tblclmnElevation.setText("Elevation");
		
		tblclmnSpeed = new TableColumn(tablePoints, SWT.RIGHT);
		tblclmnSpeed.setWidth(80);
		tblclmnSpeed.setText("Speed");
		
		tblclmnNewColumn = new TableColumn(tablePoints, SWT.RIGHT);
		tblclmnNewColumn.setWidth(100);
		tblclmnNewColumn.setText("Total Distance");
		
		tbtmMap = new TabItem(tabFolder, SWT.NONE);
		tbtmMap.setText("Map");
		
		composite_2 = new Composite(tabFolder, SWT.NONE);
		tbtmMap.setControl(composite_2);
		composite_2.setLayout(new FormLayout());
		
		comboMapType = new Combo(composite_2, SWT.READ_ONLY);
		comboMapType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				switch(comboMapType.getSelectionIndex()) {
					case 1:
						browser.execute("setBaseLayer(layers.gmap_terrain)");
						break;
					case 2:
						browser.execute("setBaseLayer(layers.gmap_satellite)");
						break;
					case 3:
						browser.execute("setBaseLayer(layers.gmap_hybrid)");
						break;
					default:
						browser.execute("setBaseLayer(layers.gmap_street)");
						break;
				};
				browser.setFocus();
			}
		});
		comboMapType.setItems(new String[] {"Streets", "Terrain", "Satellite", "Hybrid"});
		FormData fd_comboMapType = new FormData();
		fd_comboMapType.bottom = new FormAttachment(100, -10);
		fd_comboMapType.right = new FormAttachment(100, -10);
		comboMapType.setLayoutData(fd_comboMapType);
		comboMapType.select(0);
		
		Label lblNewLabel = new Label(composite_2, SWT.NONE);
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(comboMapType, 3, SWT.TOP);
		fd_lblNewLabel.right = new FormAttachment(comboMapType, -6);
		lblNewLabel.setLayoutData(fd_lblNewLabel);
		lblNewLabel.setText("Map type:");
		
		browser = new Browser(composite_2, SWT.NONE);
		FormData fd_browser = new FormData();
		fd_browser.bottom = new FormAttachment(0, 505);
		fd_browser.right = new FormAttachment(0, 776);
		fd_browser.top = new FormAttachment(0);
		fd_browser.left = new FormAttachment(0);
		browser.setLayoutData(fd_browser);
		browser.setUrl(app.getJarURI()+ "html/map.html");

		resetUI();
		btnSelectAGpx.setFocus();
	}

	protected void checkTabFocus() {
		if(tabFolder.getSelection()[0].equals(tbtmMap)) {
			browser.setFocus();
		}
	}

	public Text getTextFilename() {
		return textFilename;
	}
	public Table getTablePoints() {
		return tablePoints;
	}
	
	protected void resetUI() {
		textTimeStarted.setText("--");
		textTimeDuration.setText("--");
		textTotalDistance.setText("--");
		textAverageSpeed.setText("--");
		textAveragePace.setText("--");
		textTotalClimb.setText("--");
		tablePoints.removeAll();
		browser.execute("resetMap()");
		browser.redraw();
	}
	
	protected void newFileLoaded() {
		GPSTrack a = app.getActiveTrack();
		
		// Update the overview
		textTimeStarted.setText(a.getFirstPoint().getTime().toString(dateFormat));
		textTimeDuration.setText(String.format("%02d:%02d:%02d", a.getTrackTotalDuration().getHours(), a.getTrackTotalDuration().getMinutes(), a.getTrackTotalDuration().getSeconds()));
		textTotalDistance.setText(longDistanceFormatter.format(a.getTrackTotalDistance()/1000.0));
		textAverageSpeed.setText(longDistanceFormatter.format(a.getAverageSpeed() * 3.6)+"/h");
		textAveragePace.setText(String.format("%d:%02d min/km", a.getAveragePace().getMinutes(), a.getAveragePace().getSeconds()));
		textTotalClimb.setText(shortDistanceFormatter.format(a.getTrackTotalClimb()));
		
		// Update the log table
		
		Double runningTotal = 0.0;
			
		for(int i = 0; i < app.getActiveTrack().getTrackLength(); i++) {
				
			TableItem t = new TableItem(getTablePoints(), SWT.NONE);
			GPSPoint p = app.getActiveTrack().getPoint(i);
				
			if(i>0) {
				runningTotal += p.distanceTo(app.getActiveTrack().getPoint(i-1));
			}
				
			t.setText(new String[] {
				p.getTime().toString(dateFormat),
				String.valueOf(degreesFormatter.format(p.getLatitude())),
				String.valueOf(degreesFormatter.format(p.getLongitude())),
				String.valueOf(shortDistanceFormatter.format(p.getElevation())),
				String.valueOf(String.format("%.02f", app.getActiveTrack().getStepSpeed(i)) + " m/s"),
				String.valueOf(shortDistanceFormatter.format(runningTotal)),
			});
		}
		
		// Update the map
		String startPoint = a.getFirstPoint().getLatitude().toString()+","+a.getFirstPoint().getLongitude().toString();
		String finishPoint = a.getLastPoint().getLatitude().toString()+","+a.getLastPoint().getLongitude().toString();
		browser.execute("setMapCenter("+startPoint+",16)");
		browser.execute("addFinishMarker("+finishPoint+")");
		browser.execute("addStartMarker("+startPoint+")");
		
		for(int i = 0; i < a.getTrackLength(); i++) {
			browser.execute("addTrackPoint("+a.getPoint(i).getLatitude().toString()+","+a.getPoint(i).getLongitude().toString()+")");
		}
		browser.execute("drawTrack()");
		browser.redraw();
	}
	public Browser getBrowser() {
		return browser;
	}
	public Combo getComboMapType() {
		return comboMapType;
	}
}
