package com.dilaraalk.category.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false,unique = true)
	private String name;
	
	@Column(nullable = false,unique = true)
	private String slug;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_id") //bulundugu satırın hangi kategorinin alt kategorisi oldugunu gosterir
	private Category parent;

	
	
	

}
