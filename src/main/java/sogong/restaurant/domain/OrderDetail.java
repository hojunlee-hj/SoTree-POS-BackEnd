package sogong.restaurant.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDetail {

    @Id
    @Column(name = "OrderDetailId")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;

    // 낙관적 lock
    @Version
    private Integer version;
    
    @ManyToOne
    @JoinColumn(name = "MenuId")
    private Menu menu;

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private MenuOrder menuOrder;

}
