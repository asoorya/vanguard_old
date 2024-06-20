package vanguard.xmlparser.repository.modal;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;


@Entity
@Builder
@Getter
@AllArgsConstructor
@RequiredArgsConstructor
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String buyerParty;
    private String sellerParty;
    private double premiumAmount;
    private String premiumCurrency;


}
