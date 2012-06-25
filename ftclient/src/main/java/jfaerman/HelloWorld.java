package jfaerman;

import static org.infinispan.eviction.EvictionStrategy.LIRS;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
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
import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachelistener.annotation.CacheEntryModified;
import org.infinispan.notifications.cachelistener.event.CacheEntryModifiedEvent;

public class HelloWorld extends JApplet {
	private static final long serialVersionUID = -6223037066828034775L;
	private static DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
	private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private Cache<Object, Object> replCache;
	private Cache<Object, Object> localCache;

	final JLabel lblStatus = new JLabel("** Hello World **");
	final JLabel lblTime = new JLabel("");
	final JLabel lblEntryLocal = new JLabel("local:chave=[nada]");
	final JLabel lblEntryRepl  = new JLabel("repl:chave=[nada]");


	@Override
	public void start() {
		//TODO: Dont do so much in start()
		createLocalCache();
		createReplicatedCache();
		bindCaches();
		executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				updateUI();	
			}
		}, 10, 1, TimeUnit.SECONDS);
	}
	
	// Called when this applet is loaded into the browser.
	public void init() {
		createUI();
	}

	private void createReplicatedCache() {
		EmbeddedCacheManager manager = new DefaultCacheManager(
				GlobalConfigurationBuilder.defaultClusteredBuilder()
						.transport()
						.defaultTransport()
						.build(), new ConfigurationBuilder().clustering()
						.cacheMode(CacheMode.REPL_SYNC).build());
		replCache = manager.getCache();
	}

	private void createLocalCache() {
		EmbeddedCacheManager manager = new DefaultCacheManager();
		manager.defineConfiguration("local-cache", new ConfigurationBuilder()
				.clustering().cacheMode(CacheMode.LOCAL).build());
		localCache = manager.getCache("local-cache");
	}
	
	@Listener
	public class CopyListener{
		@CacheEntryModified
		public void onCacheEntryModified(CacheEntryModifiedEvent<String, String> event){
			if(event.isPre()) return;
			String key = event.getKey();
			String value = event.getValue();
			replCache.put(key,value);
		}
	}
	
	private void bindCaches(){
		localCache.addListener(new CopyListener());
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
					pnl.add(lblEntryLocal);
					JButton btnLocal = new JButton("Do The Local Trick");
					btnLocal.addActionListener(new ActionListener() {

						public void actionPerformed(ActionEvent e) {
							doTheLocalTrick();
						}
					});
					
					pnl.add(btnLocal);
					
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
					lblEntryLocal.setText("local:chave="+localCache.get("chave"));
					lblEntryRepl.setText("repl:chave="+replCache.get("chave"));
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void doTheReplTrick(){
		replCache.put("chave", "the repl trick done @ "+sdf.format(new Date()));
		lblStatus.setText("The repl trick is done");
	}
	
	public void doTheLocalTrick(){
		localCache.put("chave", "the local trick done @ "+sdf.format(new Date()));
		lblStatus.setText("The local trick is done");
	}
}
