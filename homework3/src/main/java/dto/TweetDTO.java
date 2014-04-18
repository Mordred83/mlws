package dto;

import java.util.Date;


public class TweetDTO {

	private static final long serialVersionUID = -3344092100206910672L;
	
	public long idStatus;
	public long idUsers;
	public String nome;
	public String tweet;
	public Date data;
	public Integer tot_eng;
	public String rt;
	
		
	public long getIdStatus() {
		return idStatus;
	}
	public void setIdStatus(long idStatus) {
		this.idStatus = idStatus;
	}
	public long getIdUsers() {
		return idUsers;
	}
	public void setIdUsers(long idUsers) {
		this.idUsers = idUsers;
	}
	public String getTweet() {
		return tweet;
	}
	public void setTweet(String tweet) {
		this.tweet = tweet;
	}

	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}	
	
	public int getTot_eng() {
		return tot_eng;
	}
	public void setTot_eng(int tot_eng) {
		this.tot_eng = tot_eng;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getRt() {
		return rt;
	}
	public void setRt(String rt) {
		this.rt = rt;
	}
	public void setTot_eng(Integer tot_eng) {
		this.tot_eng = tot_eng;
	}
	
}
