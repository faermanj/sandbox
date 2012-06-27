package jfaerman;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.Address;

public class InfinispanApplet extends JApplet {
	private static final long serialVersionUID = -6223037066828034775L;
	private static DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	final JLabel lblStatus = new JLabel("** Hello World **");
	final JLabel lblTime = new JLabel("");
	final JLabel lblEntryRepl  = new JLabel("repl:chave=[nada]");
	final JLabel lblView = new JLabel("[]");
	private CacheActor actor;

	class CacheActor implements Runnable {
		private BlockingQueue<AbstractMap.SimpleEntry<Object,Object>> queue = new LinkedBlockingQueue<AbstractMap.SimpleEntry<Object,Object>>();
		private Cache<Object, Object> replCache;
		EmbeddedCacheManager manager;
		public void createCache() {
			manager = new DefaultCacheManager(
					GlobalConfigurationBuilder.defaultClusteredBuilder()
							.transport()
							.defaultTransport()
							.build(), new ConfigurationBuilder().clustering()
							.cacheMode(CacheMode.REPL_SYNC).build());
			replCache = manager.getCache();
		}
		
		public void run() {
			createCache();
			while(true){
				try {
					AbstractMap.SimpleEntry<Object,Object> entry = queue.take();
					replCache.put(entry.getKey(), entry.getValue());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		public void put(String key,String value){
			try {
				SimpleEntry<Object, Object> entry = new AbstractMap.SimpleEntry<Object,Object>(key,value);
				queue.put(entry);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		public Object get(String key){
			return replCache!=null? replCache.get(key) : null;
		}
		
		public String getViewString(){
			if (manager==null) return "[-]";
			StringBuilder str = new StringBuilder();
			List<Address> members = manager.getMembers();
			for(Address member:members){
				str.append(member+", ");
			}
			return str.toString();
		}
	}
	
	@Override
	public void start() {
		actor = new CacheActor();
		new Thread(actor).start();
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				updateUI();	
			}
		}, 0, 1, TimeUnit.SECONDS);
	}
	
	// Called when this applet is loaded into the browser.
	public void init() {
		createUI();
	}
	

	public void createUI() {
		// Execute a job on the event-dispatching thread; creating this applet's
		// GUI.
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					JPanel pnl = new JPanel();
					pnl.setLayout(new BoxLayout(pnl, BoxLayout.PAGE_AXIS));
					pnl.add(lblStatus);
					pnl.add(lblTime);
					pnl.add(lblView);
					
					pnl.add(lblEntryRepl);
					JButton btnRepl = new JButton("Do The Repl Trick");
					btnRepl.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							doTheReplTrick();
						}
					});
					pnl.add(btnRepl);
					
		
					add(pnl);
				}
			});
		} catch (Exception e) {
			System.err.println("createGUI didn't complete successfully");
		}
	}

	public void updateUI() {
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					lblTime.setText("Current time: " + sdf.format(new Date()));
					lblEntryRepl.setText("repl:chave="+actor.get("chave"));
					lblView.setText(actor.getViewString());
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void doTheReplTrick(){
		actor.put("chave", "the repl trick done @ "+sdf.format(new Date()));
		lblStatus.setText("The repl trick is done");
	}
	
}
