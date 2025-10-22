#!/bin/bash

echo "=========================================="
echo "COBOL vs Java Comparison Test"
echo "=========================================="
echo ""

rm -f test-file-*.txt merge-output.txt sorted-contract-id.txt
rm -f cobol-merge-output.txt cobol-sorted-contract-id.txt

echo "Running COBOL program..."
cd ..
cobc -x -o merge_sort_test merge_sort_test.cbl 2>&1
if [ $? -ne 0 ]; then
    echo "Error: Failed to compile COBOL program. Is GnuCOBOL (cobc) installed?"
    exit 1
fi

./merge_sort_test > /dev/null

mv merge-output.txt cobol-merge-output.txt
mv sorted-contract-id.txt cobol-sorted-contract-id.txt

echo "Running Java program..."
cd java_implementation
java MergeSortExample > /dev/null

echo ""
echo "Comparing merge outputs..."
if diff -q merge-output.txt ../cobol-merge-output.txt > /dev/null; then
    echo "✓ Merge outputs are IDENTICAL"
else
    echo "✗ Merge outputs DIFFER"
    diff merge-output.txt ../cobol-merge-output.txt
    exit 1
fi

echo "Comparing sorted outputs..."
if diff -q sorted-contract-id.txt ../cobol-sorted-contract-id.txt > /dev/null; then
    echo "✓ Sorted outputs are IDENTICAL"
else
    echo "✗ Sorted outputs DIFFER"
    diff sorted-contract-id.txt ../cobol-sorted-contract-id.txt
    exit 1
fi

echo ""
echo "=========================================="
echo "SUCCESS: COBOL and Java produce identical outputs!"
echo "=========================================="

rm -f ../cobol-merge-output.txt ../cobol-sorted-contract-id.txt
rm -f ../merge_sort_test
