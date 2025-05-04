#!/bin/bash

set -e

ZIP_FILE="project-backup.zip"
PROJECT_ROOT="$(pwd)"
EXCLUDED_DIRS="node_modules build dist .git .gradle .idea target out gradle"
EXCLUDED_EXTENSIONS=".iml .zip"
INCLUDE_DIRS="$@"

rm -f "$ZIP_FILE"

TMP_DIR=$(mktemp -d)
trap 'rm -rf "$TMP_DIR"' EXIT

resolve_folder() {
  name="$1"
  clean_name=$(printf "%s" "$name" | sed 's|[\\/]$||' | sed 's|^\./||' | sed 's|^\.\\||')

  try_paths="$PROJECT_ROOT/$clean_name
$PROJECT_ROOT/modules/$clean_name
$PROJECT_ROOT/agents/$clean_name"

  for path in $try_paths; do
    if [ -d "$path" ]; then
      echo "$path"
      return 0
    fi
  done

  echo "Folder nie znaleziony: $name" >&2
  return 1
}

included=0
excluded=0
file_list=$(mktemp)

if [ "$#" -eq 0 ]; then
  find "$PROJECT_ROOT" -type f > "$file_list"
else
  > "$file_list"
  for dir in "$@"; do
    if path=$(resolve_folder "$dir" 2>/dev/null); then
      find "$path" -type f >> "$file_list"
      echo "Dodano folder: $path"
    fi
  done
fi

while IFS= read -r file; do
  rel_path="${file#$PROJECT_ROOT/}"

  skip_dir=false
  IFS='/' read -r -a parts <<< "$rel_path"
  for part in "${parts[@]}"; do
    for excl in $EXCLUDED_DIRS; do
      [ "$part" = "$excl" ] && skip_dir=true && break 2
    done
  done

  if [ "$skip_dir" = true ]; then
    echo "Pominięto (folder): $rel_path"
    excluded=$((excluded + 1))
    continue
  fi

  for ext in $EXCLUDED_EXTENSIONS; do
    case "$rel_path" in
      *"$ext") echo "Pominięto (rozszerzenie): $rel_path"; excluded=$((excluded + 1)); continue 2 ;;
    esac
  done

  zip -q --grow "$ZIP_FILE" "$rel_path"
  echo "Dodano: $rel_path"
  included=$((included + 1))
done < "$file_list"

echo ""
echo "Gotowe!"
echo "Spakowano $included plików. Pominięto $excluded."
