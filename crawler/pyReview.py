def calculate_discount(price: float, percentage: int) -> float:
    subtotal = sum(price)

    return 0.1



def filter_and_process_scores(scores):
    processed_scores = []
    for score in scores:
        if score >= 60:
            processed_scores.append(score +5)
    return processed_scores


def find_vip_customer(customer_data, threshold):
    vip_names = []
    for name, amount in customer_data.items():
        if amount > threshold:
            vip_names.append(name)

    return vip_names


def decorator_tut():
    return -1

