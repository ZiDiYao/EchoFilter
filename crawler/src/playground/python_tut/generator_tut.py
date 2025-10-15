user_ids = [1000001, 1000002, 1000003, 1000004, 1000005]

def process_user_list(ids):
    processed_data = []
    for user_id in ids:
        processed_data.append(f"user_{user_id}_encrypted")
    return processed_data


def process_user_generator(ids):
    print("--- Start Generator ---")
    for user_id in ids:
        # 当执行到 yield 时，函数暂停并返回当前值
        yield f"user_{user_id}_encrypted"
        print(f"--- DONE ID {user_id} ---")

# Generate a generator
user_generator = process_user_generator(user_ids)

# How to use Generator ?
print("1...")
print(next(user_generator))

print("\n2...")
print(next(user_generator))

print("\n Left..")
for user_data in user_generator:
    print(user_data)
