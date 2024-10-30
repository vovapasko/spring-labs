package rewards.internal;

import rewards.Dining;
import rewards.RewardConfirmation;
import rewards.RewardNetwork;
import rewards.internal.account.AccountRepository;
import rewards.internal.restaurant.RestaurantRepository;
import rewards.internal.reward.RewardRepository;

/**
 * Rewards an Account for Dining at a Restaurant.
 * <p>
 * The sole Reward Network implementation. This object is an application-layer service responsible for coordinating with
 * the domain-layer to carry out the process of rewarding benefits to accounts for dining.
 * <p>
 * Said in other words, this class implements the "reward account for dining" use case.
 * <p>
 */
public class RewardNetworkImpl implements RewardNetwork {

    private AccountRepository accountRepository;

    private RestaurantRepository restaurantRepository;

    private RewardRepository rewardRepository;

    /**
     * Creates a new reward network.
     *
     * @param accountRepository    the repository for loading accounts to reward
     * @param restaurantRepository the repository for loading restaurants that determine how much to reward
     * @param rewardRepository     the repository for recording a record of successful reward transactions
     */
    public RewardNetworkImpl(AccountRepository accountRepository, RestaurantRepository restaurantRepository,
                             RewardRepository rewardRepository) {
        this.accountRepository = accountRepository;
        this.restaurantRepository = restaurantRepository;
        this.rewardRepository = rewardRepository;
    }

    public RewardConfirmation rewardAccountFor(Dining dining) {
        var account = accountRepository.findByCreditCard(dining.getCreditCardNumber());
        var restaurant = restaurantRepository.findByMerchantNumber(dining.getMerchantNumber());
        var calculatedAmount = restaurant.calculateBenefitFor(account, dining);
        var commitedContribution = account.makeContribution(calculatedAmount);
        return rewardRepository.confirmReward(commitedContribution, dining);
    }
}