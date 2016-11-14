package com.faiza.wiktionary.swt;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.faiza.wiktionary.Word;
import com.faiza.wiktionary.service.Dictionary;
import com.faiza.wiktionary.service.impl.DictionaryImpl;

import info.bliki.wiki.model.WikiModel;

public class WiktionaryApp {
	
	private static final Logger LOG = LoggerFactory.getLogger(WiktionaryApp.class);

	private Text searchField;

	private Button searchButton;
	
	private Button anagramsButton;

	private Browser browser;

	private Dictionary dictionary;

	public WiktionaryApp(){
		this.dictionary = new DictionaryImpl();
		Label label;
		GridData gridData;
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Faiza Wiktionary");
		shell.setBounds(100,100,500,500);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		shell.setLayout(layout);

		final WiktionaryApp objectReference = this;
		
		label = new Label(shell, SWT.LEFT);
		label.setText("Enter word to see it's definition");
		gridData = new GridData();
		gridData.horizontalSpan = 4;
		label.setLayoutData(gridData);

		gridData = new GridData();
		gridData.horizontalSpan = 1;
		label = new Label(shell, SWT.LEFT);
		label.setText("Find:");
		label.setLayoutData(gridData);
		this.searchField = new Text(shell, SWT.SINGLE | SWT.BORDER);
		this.searchField.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event){
				if(event.keyCode == 13){
					objectReference.searchWord();
				}
			}
		});
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalSpan = 3;
		this.searchField.setLayoutData(gridData);
		
		
		this.searchButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		this.searchButton.setText("Search");
		this.searchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				objectReference.searchWord();
			}
		});
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		//gridData.horizontalSpan = 2;
		this.searchButton.setLayoutData(gridData);
		
		this.anagramsButton = new Button(shell, SWT.PUSH | SWT.CENTER);
		this.anagramsButton.setText("Show Anagrams");
		this.anagramsButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				objectReference.searchAnagrams();
			}
		});
		gridData = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		//gridData.horizontalSpan = 2;
		this.anagramsButton.setLayoutData(gridData);


		this.browser = new Browser(shell, SWT.BORDER | SWT.LEFT);
		this.browser.setBounds(110, 150, 480, 350);
		this.browser.setText("<html><body style=\"font-family:Helvetica\"><h2>English Language dictionary</h2>"
				+ "<h5>Backed by Wiktionary &nbsp;<i>[ A Wikimedia Project ]</i></h5> </body></html>"); 
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		this.browser.setLayoutData(gridData);

		shell.open();
		while(!shell.isDisposed()){
			if(!display.readAndDispatch()) display.sleep();
		}
		display.dispose();
	}

	public void clearBrowser(){
		this.browser.setText("");
	}

	public void setHTML(String htmlString){
		this.browser.setText(htmlString, true);
	}

	public void searchWord(){
		String definition = "";
		String title = this.searchField.getText();
		List<Word> words = this.dictionary.getWords(title);
		if(words != null && !words.isEmpty()){
			definition = words.get(0).getDefinition();
			if(definition != null && !"".equals(definition)){
				definition = this.getWikiMarkupToHTML(definition);
			}
		}
		this.clearBrowser();
		this.setHTML(definition);
	}

	private String getWikiMarkupToHTML(String definition) {
		String html = "<html><body>";
		html = WikiModel.toHtml(definition);
		html += "</body></html>";
		LOG.info(html);
		return html;
	}
	
	public void searchAnagrams(){
		String result = "<html><body>";
		String title = this.searchField.getText();
		List<Word> anagrams = this.dictionary.getAnagrams(title);
		if(anagrams != null && !anagrams.isEmpty()){
			result += "<ul>";
			for(Word anagram : anagrams){
				result += "<li>"+anagram.getTitle()+"</li>";
			}
			result += "</ul>";
		}
		this.clearBrowser();
		this.setHTML(result);
	}
}
