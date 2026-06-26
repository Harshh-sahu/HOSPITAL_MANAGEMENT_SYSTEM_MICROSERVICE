import { Badge, Group, Text } from "@mantine/core";
import {
  IconClock,
  IconPhone,
  IconReceiptRupee,
} from "@tabler/icons-react";
import { formatDate } from "../../../Utility/DateUtility";

const getInitials = (name: string) =>
  name
    .split(" ")
    .map((n) => n[0])
    .join("")
    .toUpperCase()
    .slice(0, 2);

const getAmountMeta = (amount: number) => {
  if (amount >= 1000) return { bg: "bg-emerald-500", light: "bg-emerald-50", text: "text-emerald-700", badge: "green", tier: "Premium" };
  if (amount >= 500)  return { bg: "bg-blue-500",    light: "bg-blue-50",    text: "text-blue-700",    badge: "blue",  tier: "Standard" };
  return               { bg: "bg-violet-400",  light: "bg-violet-50",  text: "text-violet-700",  badge: "violet", tier: "Basic" };
};

const SaleCard = ({ id, buyerContact, saleDate, buyerName, totalAmount, onView }: any) => {
  const meta = getAmountMeta(totalAmount ?? 0);
  const initials = getInitials(buyerName ?? "?");

  return (
    <div
      onClick={onView}
      className="group relative overflow-hidden rounded-2xl border border-gray-100 bg-white shadow-sm hover:shadow-xl hover:-translate-y-1 transition-all duration-300 cursor-pointer"
    >
      {/* Color accent strip */}
      <div className={`h-1.5 w-full ${meta.bg}`} />

      <div className="p-4 flex flex-col gap-3">
        {/* Top: avatar + name + amount badge */}
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            <div className={`w-11 h-11 rounded-full ${meta.bg} flex items-center justify-center shrink-0 shadow-sm`}>
              <span className="text-white font-bold text-sm">{initials}</span>
            </div>
            <div>
              <Text fw={700} size="sm" lineClamp={1} className="text-gray-800">{buyerName}</Text>
              <Group gap={4} mt={2}>
                <IconPhone size={11} className="text-gray-400" />
                <Text size="xs" c="dimmed">+91 {buyerContact}</Text>
              </Group>
            </div>
          </div>

          <div className="flex flex-col items-end gap-1">
            <Badge variant="filled" color={meta.badge} size="md" radius="md"
              leftSection={<IconReceiptRupee size={11} />}>
              ₹{(totalAmount ?? 0).toLocaleString("en-IN")}
            </Badge>
            <Badge variant="light" color={meta.badge} size="xs">{meta.tier}</Badge>
          </div>
        </div>

        {/* Transaction ID */}
        {id && (
          <div className={`rounded-lg px-3 py-1.5 ${meta.light} flex items-center justify-between`}>
            <Text size="xs" c="dimmed" fw={500}>TXN ID</Text>
            <Text size="xs" fw={700} className={meta.text}>#{String(id).padStart(6, "0")}</Text>
          </div>
        )}

        {/* Divider */}
        <div className="border-t border-dashed border-gray-100" />

        {/* Footer */}
        <div className="flex items-center justify-between">
          <Group gap={5}>
            <IconClock size={13} className="text-gray-400" />
            <Text size="xs" c="dimmed">{formatDate(saleDate)}</Text>
          </Group>
          <Text size="xs" className={`${meta.text} font-semibold opacity-0 group-hover:opacity-100 transition-opacity`}>
            View Receipt →
          </Text>
        </div>
      </div>
    </div>
  );
};

export default SaleCard;


