import sys
import time
import random
import multiprocessing
import matplotlib.pyplot as plt


if len(sys.argv) > 1:
    ARRAY_SIZE = int(sys.argv[1])
else:
    ARRAY_SIZE = 100_000_000


def generate_data():
    return [random.random() for _ in range(ARRAY_SIZE)]


def sum_sequential(data):
    total = 0.0
    for v in data:
        total += v
    return total


def _partial_sum(data_slice):
    local_sum = 0.0
    for v in data_slice:
        local_sum += v
    return local_sum


def sum_parallel(data):
    cores = multiprocessing.cpu_count()
    chunk_size = len(data) // cores

    chunks = []
    for c in range(cores):
        start = c * chunk_size
        end = len(data) if c == cores - 1 else start + chunk_size
        chunks.append(data[start:end])

    with multiprocessing.Pool(cores) as pool:
        results = pool.map(_partial_sum, chunks)

    return sum(results)


if __name__ == "__main__":
    print("Running...")
    data = generate_data()

    # Sequential
    start = time.perf_counter()
    seq_result = sum_sequential(data)
    seq_time = (time.perf_counter() - start) * 1000

    # Parallel
    start = time.perf_counter()
    par_result = sum_parallel(data)
    par_time = (time.perf_counter() - start) * 1000

    print("\n--- TIMES ---")
    print(f"Sequential: {seq_time:.2f} ms")
    print(f"Parallel:   {par_time:.2f} ms")
    print(f"Speedup:    {seq_time / par_time:.2f}x")

    # Bar Graph
    labels = ["Sequential", "Parallel"]
    times = [seq_time, par_time]

    plt.figure()
    plt.bar(labels, times)
    plt.title(f"Sequential vs Parallel Execution Time. Speedup: {seq_time / par_time:.2f}x")
    plt.ylabel("Time (ms)")
    plt.xlabel("Execution Type")

    for i, v in enumerate(times):
        plt.text(i, v, f"{v:.2f}", ha='center', va='bottom')

    plt.show()
