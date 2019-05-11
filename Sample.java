import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

class Contract {
  private CapacityPlan capacityPlan;
  private List<OptionPlan> optionPlans;

  public Contract(@NonNull CapacityPlan capacityPlan,
          @NonNull List<OptionPlan> optionPlan) {
    optionPlans.forEach(plan -> {
          if (!plan.getPermittedCapacityPlans().contains(capacityPlan)) {
            throw new NotPermittedCapacityPlanException("許可されていないプランです");
          }
        }
    );
    this.capacityPlan = capacityPlan;
    this.optionPlans = optionPlan;
  }

  MonthlyTotalPrice calculateTotalPrice() {
    // capacityPlanのPriceとoptionPlansすべてのPriceを足し合わせる
    Price sumPrice = this.optionPlans.stream()
        .reduce(this.capacityPlan.getPrice(), (sum, plan) -> plan.getPrice(), Price::plus);
    return new MonthlyTotalPrice(sumPrice);
  }
}


class NotPermittedCapacityPlanException extends RuntimeException {
  public NotPermittedCapacityPlanException(String msg) {
    super(msg);
  }
}

@Getter
enum CapacityPlan {
  _1GB(1000),
  _3GB(3000),
  _30GB(6000);
  private Price price;

  CapacityPlan(int price) {
    this.price = new Price(price);
  }
}

@Getter
enum OptionPlan {
  /**
   * 動画無制限プラン
   */
  MovieFree(
      1000,
      Arrays.asList(CapacityPlan._3GB, CapacityPlan._30GB)
  ),
  /**
   * 電話し放題プラン
   */
  CallFree(
      3000,
      Arrays.asList(CapacityPlan._1GB, CapacityPlan._3GB, CapacityPlan._30GB)
  );

  private Price price;
  private List<CapacityPlan> permittedCapacityPlans;

  OptionPlan(int price, List<CapacityPlan> permittedCapacityPlans) {
    this.price = new Price(price);
    this.permittedCapacityPlans = permittedCapacityPlans;
  }

}

class Price {
  private int value;

  Price(int value) {
    this.value = value;
  }

  int intValue() {
    return value;
  }

  Price plus(Price price) {
    return new Price(this.value + price.intValue());
  }
}


@Getter
class MonthlyTotalPrice {
  private Price value;

  MonthlyTotalPrice(Price price) {
    this.value = price;
  }
}
