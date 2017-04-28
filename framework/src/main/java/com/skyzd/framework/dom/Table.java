package com.skyzd.framework.dom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class Table {
	private List<Tr> trs;
	private String html;
	private String[][] table;
	private boolean isHtml;
	public class Tr {
		private List<Td> tds;

		public List<Td> getTds() {
			return tds;
		}

		public void setTds(List<Td> tds) {
			this.tds = tds;
		}
		
	}
	public class Td {
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
	}
	public Table(String tableHtml){
		html=tableHtml;
	}
	
	public void parse1(){
		Document document=Jsoup.parse(html);
		Elements trsE = document.select("tr");
		Elements firstTds=trsE.get(0).select("td");
		table = new String[trsE.size()][firstTds.size()];
		for (int row = 0; row < table.length; row++) {
			Element tr = trsE.get(row);
			Elements tds = tr.select("td");
			int column = 0;
			for (Element td : tds) {
				if (td.hasAttr("rowspan")) {
					int rowspan = Integer.parseInt(td.attr("rowspan"));
					td.removeAttr("rowspan");
					int endRow = rowspan + row - 1;
					for (int startColumn=column; endRow > row; endRow--) {
						boolean flag = true;
						int columnA=startColumn;
						while (flag) {
							if (table[endRow][columnA] == null) {
								table[endRow][columnA] = isHtml?td.toString():td.html();
								flag=false;
								startColumn=columnA;
							} else {
								columnA++;
							}
						}

					}
				}
				boolean runing = true;
				do {
					if (table[row][column] == null) {
						runing = false;
						table[row][column] = isHtml?td.toString():td.html();
					} else {
						runing = true;
					}
					column++;
				} while (runing);

			}
		}
		trs=new ArrayList<Tr>();
		for (String[] strings : table) {
			Tr tr=new Tr();
			List<Td> tds=new ArrayList<Td>();
			for (String string : strings) {
				Td td=new Td();
				td.setContent(string);
				tds.add(td);
			}
			tr.setTds(tds);
			trs.add(tr);
		}
	}
	
	public void parse(){
		Document document = Jsoup.parse(html);
		Elements trsE = document.select("tr");
		int maxTrsSize = 0;
		int maxTdsSize = 0;
		boolean isFirst = true;
		for (Element trE : trsE) {
			maxTrsSize++;
			int tdsSize = 0;
			Elements tdsE = null;
			if (isFirst) {
				tdsE = trE .select("th");
				isFirst = false;
			}
			if (tdsE == null || tdsE.isEmpty()) {
				tdsE = trE .select("td");
			}
			int maxColSpan = 0;
			for (Element tdE : tdsE) {
				tdsSize++;
				if(tdE.hasAttr("colspan")) {
					int colspan = Integer.parseInt(tdE.attr("colspan"));
					if (colspan > maxColSpan) {
						maxColSpan = colspan;
					}
				}
			}
			int incrementCol = maxColSpan - 1;
			if (incrementCol > 0) {
				tdsSize += incrementCol; 
			}
			if (tdsSize > maxTdsSize) {
				maxTdsSize = tdsSize;
			}
		}
		table = new String[maxTrsSize][maxTdsSize];
		isFirst = true;
		for (int row = 0; row < table.length; row++) {
			Element tr = trsE.get(row);
			Elements tds = null;
			if (isFirst) {
				tds = tr .select("th");
				isFirst = false;
			}
			if (tds == null || tds.isEmpty()) {
				tds = tr .select("td");
			}
			int column = 0;
			int tdCol = -1;
			for (Element td : tds) {
				tdCol++;
				int colMv = 0;
				if (td.hasAttr("colspan")) {
					int colspan = Integer.parseInt(td.attr("colspan"));
					td.removeAttr("colspan");
					colMv = colspan - 1;
					for (int i = 1; i < colspan; i++) {
						if (table[row][tdCol + i] == null) {
							table[row][tdCol + i] = isHtml?td.toString():td.html();
						}
						tdCol++;
					}
				}
				if (td.hasAttr("rowspan")) {
					int rowspan = Integer.parseInt(td.attr("rowspan"));
					td.removeAttr("rowspan");
					int endRow = rowspan + row - 1;
					for (int startColumn=column; endRow > row; endRow--) {
						boolean flag = true;
						int columnA=startColumn;
						while (flag) {
							if (table[endRow][columnA] == null) {
								table[endRow][columnA] = isHtml?td.toString():td.html();
								flag=false;
								startColumn=columnA;
							} else {
								columnA++;
							}
						}
					}
				}
				boolean runing = true;
				do {
					if (table[row][column] == null) {
						runing = false;
						table[row][column] = isHtml?td.toString():td.html();
					} else {
						runing = true;
					}
					column++;
					column += colMv;
				} while (runing);
			}
		}
		trs = new ArrayList<Tr>();
		for (String[] strings : table) {
			Tr tr=new Tr();
			List<Td> tds=new ArrayList<Td>();
			for (String string : strings) {
				Td td=new Td();
				td.setContent(string);
				tds.add(td);
			}
			tr.setTds(tds);
			trs.add(tr);
		}
	}
	public List<Tr> getTrs(){
		return trs;
	}
	public String getHtml(){
		return html;
	}
	public int getTrCount(){
		return trs.size();
	}
	public void isHtml(boolean v){
		isHtml=v;
	}
}
