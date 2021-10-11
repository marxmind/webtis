package com.italia.municipality.lakesebu.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.TabChangeEvent;

import com.italia.municipality.lakesebu.controller.Article;
import com.italia.municipality.lakesebu.controller.ArticleParticular;
import com.italia.municipality.lakesebu.controller.ArticleSubtype;
import com.italia.municipality.lakesebu.controller.Login;
import com.italia.municipality.lakesebu.controller.PaymentName;
import com.italia.municipality.lakesebu.enm.AccessLevel;
import com.italia.municipality.lakesebu.utils.Application;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Mark Italia
 * @since 10/07/2021
 * @version 1.0
 *
 */
@Named
@ViewScoped
public class ArticleBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 54354675465431L;
	
	@Setter @Getter  private List<Article> articles;
	@Setter @Getter  private String searchArticle;
	@Setter @Getter  private boolean normalUser;
	
	@Setter @Getter  private List<ArticleSubtype> subArticles;
	@Setter @Getter  private String searchSubArticle;
	
	@Setter @Getter  private List arts;
	@Setter @Getter  private List subArts;
	
	
	@Setter @Getter  private List<ArticleParticular> particulars;
	
	public void openRevenue() {
		
		
		Login in = Login.getUserLogin();
		 if(AccessLevel.DEVELOPER.getId()==in.getAccessLevel().getLevel() || AccessLevel.ADMIN.getId()==in.getAccessLevel().getLevel()) {
			 setNormalUser(true);
		 }
		 
		 arts = new ArrayList<>();
		 arts.add(new SelectItem(0, "Select Article"));
		 for(Article a : Article.retrieve("",new String[0])) {
			 arts.add(new SelectItem(a.getId(), a.getCode()));
		 }
		 
		 subArts = new ArrayList<>();
		 subArts.add(new SelectItem(0, "Select Sub Article"));
		 for(ArticleSubtype a : ArticleSubtype.retrieve("",new String[0])) {
			 subArts.add(new SelectItem(a.getId(), a.getName()));
		 }
		 
		 PrimeFaces pf = PrimeFaces.current();
		 pf.executeScript("PF('dlgArticle').show(1000)");
		 loadArticle();
	}
	
	public void loadArticle() {
		articles = new ArrayList<Article>();
		String sql = "";
		if(getSearchArticle()!=null && !getSearchArticle().isEmpty()) {
			sql = " AND (st.artcode like '%"+ getSearchArticle() +"%' OR st.artname like '%"+ getSearchArticle() +"%')";
		}
		
		//add default value colum for input
		articles.add(Article.builder()
				.code("Add Code")
				.name("Add Name")
				.description("Add Description")
				.isActive(1)
				.build());
		
		articles.addAll(Article.retrieve(sql + " ORDER BY st.artcode", new String[0]));
	}
	
	public void loadSub() {
		subArticles = new ArrayList<ArticleSubtype>();
		String sql = "";
		if(getSearchSubArticle()!=null && !getSearchSubArticle().isEmpty()) {
			sql = " AND sub.subname like '%"+ getSearchSubArticle() +"%'";
		}
		
		//add default value colum for input
		subArticles.add(ArticleSubtype.builder()
				.name("Add Name")
				.articleId(0)
				.articles(getArts())
				.isActive(1)
				.build());
		
		subArticles.addAll(ArticleSubtype.retrieve(sql + " ORDER BY sub.subname", new String[0]));
	}
	
	public void loadParticulars() {
		
	}
	
	public void loadAll() {
		
	}
	
	public void onChange(TabChangeEvent event) {
		//Tab activeTab = event.getTab();
		//...
		if("Article".equalsIgnoreCase(event.getTab().getTitle())) {
			loadArticle();
		}else if("Sub Article".equalsIgnoreCase(event.getTab().getTitle())){
			loadSub();
		}else if("Particulars".equalsIgnoreCase(event.getTab().getTitle())){
			loadParticulars();
		}else if("Revenue Code Details".equalsIgnoreCase(event.getTab().getTitle())){
			loadAll();
		}		
	}
	
	public void onCellEditArticle(CellEditEvent event) {
		   Object oldValue = event.getOldValue();
		   Object newValue = event.getNewValue();
	}	
	
	public void onCellEditSubArticle(CellEditEvent event) {
		   Object oldValue = event.getOldValue();
		   Object newValue = event.getNewValue();
	}	
	
	public void saveArticle(Article article) {
		        if(!"Add Code".equalsIgnoreCase(article.getCode()) && !"Add Name".equalsIgnoreCase(article.getName())) {
		        	article.save();
		        	loadArticle();
		        	Application.addMessage(1, "Success", "Data was successfully saved.");
		        }else {
		        	Application.addMessage(2, "Error", "Saving was not successfully please check your data.");
		        }
		}
	public void saveArticleSub(ArticleSubtype sub) {
		if(!"Add Name".equalsIgnoreCase(sub.getName())) {
        	sub.save();
        	loadSub();
        	Application.addMessage(1, "Success", "Data was successfully saved.");
        }else {
        	Application.addMessage(2, "Error", "Saving was not successfully please check your data.");
        }
	}
}
