package com.dilaraalk.cart.entity;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.dilaraalk.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Cart {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
	private User user;
	
	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	private Set<CartItem> items = new HashSet<>();
	
	@CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

	
	public void addItem(CartItem item) {
		if (item == null) return;
        item.setCart(this);  
        items.add(item);
	}
	
	public void removeItem(CartItem item) {
        if (item == null) return;
        items.remove(item);   
        item.setCart(null);
    }
	

}
