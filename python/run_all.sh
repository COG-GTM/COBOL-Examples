#!/usr/bin/env bash
# Smoke test: run every Python port non-interactively and check it exits 0.
# Interactive programs get canned stdin; curses programs use their
# line-mode fallback (COBOL_DEMO_NO_CURSES=1).
set -u
cd "$(dirname "$0")"
export COBOL_DEMO_NO_CURSES=1
work_dir=$(mktemp -d)
trap 'rm -rf "$work_dir"' EXIT

# script | arguments | stdin
cases=(
  "accept.py||hello\n\nt1\nabcdefghijklmnop\nsecret\nupper\n"
  "accept_from.py|one two|console\n\n"
  "accept_secure.py||pw123\n"
  "comp_test.py|--explain|123\n"
  "display_test.py||"
  "display_timing.py|--runs 2 --refreshes 5|\n"
  "generated_sql_ex.py||1\n2\n3\nTester1\nn\n4\n"
  "is_numeric.py||123\n42\n 42 \n"
  "json_generate.py||"
  "main_app.py||hello\nworld\n"
  "merge_sort.py||"
  "mouse_example.py||"
  "numval_test.py||12.5-\n7\n"
  "read_cmd_line_args.py|--test foo|"
  "read_specific_cmd_line_args.py|alpha beta|"
  "redefines.py||"
  "report_writer.py|--output $work_dir/report.txt|"
  "screen_size.py||\n\n\n\n\n\n"
  "search.py||2\n2\n102\n499\n3\n"
  "sql_example.py||1\n2\n3\nTester1\nn\n4\n"
  "trim.py||"
  "unstring.py||"
  "xml_generate.py||"
)

failed=0
for entry in "${cases[@]}"; do
  IFS='|' read -r script args input <<< "$entry"
  # shellcheck disable=SC2086
  if printf "$input" | python3 "$script" $args > "$work_dir/out.log" 2>&1; then
    echo "PASS  $script"
  else
    echo "FAIL  $script (exit $?)"
    sed 's/^/      /' "$work_dir/out.log"
    failed=$((failed + 1))
  fi
done

total=$(ls ./*.py | grep -cvE '/(cobol_screen|sql_db|sub_app)\.py$')
if [ "$total" -ne "${#cases[@]}" ]; then
  echo "FAIL  $total runnable scripts but ${#cases[@]} smoke cases"
  failed=$((failed + 1))
fi

echo "${#cases[@]} scripts, $failed failure(s)"
exit $((failed > 0))
