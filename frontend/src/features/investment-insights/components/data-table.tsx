"use client";

import {
  ColumnDef,
  flexRender,
  getCoreRowModel,
  useReactTable,
} from "@tanstack/react-table";

import { colors } from "@/theme/theme";

interface DataTableProps<TData, TValue> {
  columns: ColumnDef<TData, TValue>[];
  data: TData[];
  onRowClick?: (row: TData) => void;
}

export function DataTable<TData, TValue>({
  columns,
  data,
  onRowClick,
}: DataTableProps<TData, TValue>) {
  const table = useReactTable({
    data,
    columns,
    getCoreRowModel: getCoreRowModel(),
  });

  return (
    <div style={{ background: colors.bgSurface, borderRadius: "12px", overflow: "hidden" }}>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        {/* Header */}
        <thead>
          {table.getHeaderGroups().map((headerGroup) => (
            <tr
              key={headerGroup.id}
              style={{
                background: colors.bgSurfaceAlt,
                borderBottom: `1px solid ${colors.border}`,
              }}
            >
              {headerGroup.headers.map((header) => (
                <th
                  key={header.id}
                  style={{
                    padding: "12px 20px",
                    textAlign: "left",
                    fontSize: "11px",
                    fontWeight: 600,
                    textTransform: "uppercase",
                    letterSpacing: "0.08em",
                    color: colors.textFaint,
                    whiteSpace: "nowrap",
                  }}
                >
                  {header.isPlaceholder
                    ? null
                    : flexRender(
                        header.column.columnDef.header,
                        header.getContext()
                      )}
                </th>
              ))}
            </tr>
          ))}
        </thead>

        {/* Body */}
        <tbody>
          {table.getRowModel().rows.length ? (
            table.getRowModel().rows.map((row, i) => (
              <tr
                key={row.id}
                onClick={() => onRowClick?.(row.original)}
                style={{
                  background: i % 2 === 0 ? colors.bgSurface : colors.bgSurface2,
                  borderBottom: `1px solid ${colors.border}`,
                  cursor: onRowClick ? "pointer" : "default",
                  transition: "background 0.15s ease",
                }}
                onMouseEnter={(e) => {
                  if (onRowClick)
                    (e.currentTarget as HTMLTableRowElement).style.background =
                      colors.bgHover;
                }}
                onMouseLeave={(e) => {
                  (e.currentTarget as HTMLTableRowElement).style.background =
                    i % 2 === 0 ? colors.bgSurface : colors.bgSurface2;
                }}
              >
                {row.getVisibleCells().map((cell) => (
                  <td
                    key={cell.id}
                    style={{
                      padding: "14px 20px",
                      fontSize: "13px",
                      color: colors.textPrimary,
                      verticalAlign: "middle",
                    }}
                  >
                    {flexRender(cell.column.columnDef.cell, cell.getContext())}
                  </td>
                ))}
              </tr>
            ))
          ) : (
            <tr>
              <td
                colSpan={columns.length}
                style={{
                  padding: "48px 20px",
                  textAlign: "center",
                  color: colors.textFaint,
                  fontSize: "13px",
                }}
              >
                No results.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
