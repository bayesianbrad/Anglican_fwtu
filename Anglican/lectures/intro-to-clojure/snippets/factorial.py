from __future__ import print_function
import sys

def factorial(n):
    '''computes n * (n - 1) * ... * 1'''
    if n == 1:
        return 1
    else:
        return n * factorial(n - 1)

def factorial_loop(n):
    '''computes n * (n - 1) * ... * 1'''
    result = 1
    for n in range(2, n + 1):
        result *= n
    return result

if __name__ == '__main__':
    for arg in sys.argv[1:]:
        n = int(arg)
        print('the factorial of', n, 
              'is', factorial(n))
