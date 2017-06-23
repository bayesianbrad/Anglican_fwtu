def factorial(n):
    '''computes n * (n - 1) * ... * 1'''
    result = 1
    for n in range(2, n + 1):
        result *= n
    return result

def factorial(n):
    '''computes n * (n - 1) * ... * 1'''
    result = 1
    ivals = range(2, n + 1)
    while ivals:
        i = ivals.pop(0)
        result *= i
    return result