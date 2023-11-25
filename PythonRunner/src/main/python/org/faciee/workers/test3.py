import sys

if __name__ == "__main__":
    # Access command-line parameters
    if len(sys.argv) > 1:
        param1 = sys.argv[1]
        print("Parameter 1:", param1)
    else:
        print("No parameters provided.")