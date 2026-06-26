import { useEffect, useState } from "react";
import {
  Card,
  Text,
  Loader,
  Center,
  Stack,
  Group,
  TextInput,
  Badge,
  SimpleGrid,
} from "@mantine/core";
import { IconPill, IconSearch } from "@tabler/icons-react";
import { getAllMedicines } from "../../../Service/MedicineService";

const MedicineReference = () => {
  const [medicines, setMedicines] = useState<any[]>([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getAllMedicines()
      .then((data) => setMedicines(data))
      .catch((err) => console.error("Error fetching medicines:", err))
      .finally(() => setLoading(false));
  }, []);

  const filtered = medicines.filter((m: any) =>
    (m.name ?? m.medicineName ?? "")
      .toLowerCase()
      .includes(search.toLowerCase())
  );

  if (loading) {
    return (
      <Center h={300}>
        <Loader color="primary" />
      </Center>
    );
  }

  return (
    <Stack gap="md">
      <TextInput
        placeholder="Search medicines..."
        leftSection={<IconSearch size={16} />}
        value={search}
        onChange={(e) => setSearch(e.currentTarget.value)}
        radius="md"
      />

      {filtered.length === 0 ? (
        <Center h={200}>
          <Stack align="center" gap={8}>
            <IconPill size={48} stroke={1.2} className="text-gray-300" />
            <Text c="dimmed">No medicines found.</Text>
          </Stack>
        </Center>
      ) : (
        <SimpleGrid cols={{ base: 1, sm: 2, md: 3 }} spacing="md">
          {filtered.map((med: any, index: number) => {
            const name = med.name ?? med.medicineName ?? `Medicine #${index + 1}`;
            const category = med.category ?? med.type ?? null;
            const price = med.price != null ? `₹${med.price}` : null;
            const manufacturer = med.manufacturer ?? null;

            return (
              <Card key={med.id ?? index} withBorder radius="md" p="md" shadow="xs">
                <Group justify="space-between" align="flex-start" mb={6}>
                  <Group gap={8}>
                    <div className="p-1.5 rounded-md bg-primary-50">
                      <IconPill size={16} className="text-primary-500" />
                    </div>
                    <Text fw={600} size="sm" lineClamp={1}>
                      {name}
                    </Text>
                  </Group>
                  {category && (
                    <Badge variant="light" color="primary" size="xs">
                      {category}
                    </Badge>
                  )}
                </Group>
                {manufacturer && (
                  <Text size="xs" c="dimmed">
                    {manufacturer}
                  </Text>
                )}
                {price && (
                  <Text size="xs" fw={500} className="text-primary-600" mt={4}>
                    {price}
                  </Text>
                )}
              </Card>
            );
          })}
        </SimpleGrid>
      )}
    </Stack>
  );
};

export default MedicineReference;
