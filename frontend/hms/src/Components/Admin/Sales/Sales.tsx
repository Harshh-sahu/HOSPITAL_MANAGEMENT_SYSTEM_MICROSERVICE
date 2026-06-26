import {
  ActionIcon,
  Avatar,
  Badge,
  Button,
  Card,
  Divider,
  Fieldset,
  Grid,
  Group,
  LoadingOverlay,
  Modal,
  NumberInput,
  SegmentedControl,
  Select,
  SelectProps,
  SimpleGrid,
  Text,
  TextInput,
  ThemeIcon,
  Title,
  Tooltip,
} from "@mantine/core";
import React, { use, useEffect, useState } from "react";

import {
  IconCalendar,
  IconCheck,
  IconDashboard,
  IconDownload,
  IconEye,
  IconFileText,
  IconFileTypePdf,
  IconHome,
  IconLayoutGrid,
  IconPhone,
  IconPlus,
  IconReceiptRupee,
  IconSearch,
  IconSearchOff,
  IconShoppingBag,
  IconShoppingCart,
  IconTable,
  IconTrash,
  IconTrendingUp,
} from "@tabler/icons-react";
import { useForm } from "@mantine/form";
import {
  errorNotification,
  successNotification,
} from "../../../Utility/NotificationUtil";
import { FilterMatchMode } from "primereact/api";
import { DataTable, DataTableFilterMeta } from "primereact/datatable";
import { Column } from "primereact/column";
import {

  getAllMedicines,

} from "../../../Service/MedicineService";

import { addSale, getAllSaleItem, getAllsales } from "../../../Service/SalesService";
import { formatDate } from "../../../Utility/DateUtility";
import { useDisclosure, useMediaQuery } from "@mantine/hooks";
import { spotlight, Spotlight, SpotlightActionData } from "@mantine/spotlight";
import { getAllPrescriptions, getMedicinesByPrescriptionId } from "../../../Service/AppointmentService";
import { freqMap } from "../../../Data/DropDownData";
import { Toolbar } from "primereact/toolbar";
import MedCard from "../Medicine/MedCard";
import SaleCard from "./SaleCard";
import { exportToCSV, generateReceiptPDF } from "../../../Utility/ExportUtil";

interface SaleItem {
  medicineId: string;
  quantity: number;
}
const Sales = () => {
  const [loading, setLoading] = React.useState(false);
  const [view, setView] = useState<string>("table");
  const form = useForm<any>({
    initialValues: {
      buyerName: "",
      buyerContact: "",
      saleItems: [
        { medicineId: "", quantity: 0 }
      ] as SaleItem[],
    },
    validate: {
      saleItems: {
        medicineId: (value) => (value ? null : "Medicine ID is required"),

        quantity: (value) =>
          value > 0 ? null : "Quantity cannot be negative",
      },
    },
  });
   const [opened,{ open, close }] = useDisclosure(false);
    const [saleItems,setSaleItems] = useState<any[]>([]);
  const [data, setData] = useState<any[]>([]);
  const [Medicine, setMedicine] = useState<any[]>([]);
  const [edit, setEdit] = useState<boolean>(false);
  const [filters, setFilters] = useState<DataTableFilterMeta>({
    global: { value: null, matchMode: FilterMatchMode.CONTAINS },
  });
  const [actions, setActions] = useState<SpotlightActionData[]>([]);

  const cancel = () => {
    form.reset();
    setEdit(false);
  };

  const addMore = () => {
    form.insertListItem("saleItems", { medicineId: "", quantity: 0 });
  };

  const [medicineMap, setMedicineMap] = useState<Record<string, any>>({});

  const [globalFilterValue, setGlobalFilterValue] = useState<string>("");

  useEffect(() => {
    getAllMedicines()
      .then((res) => {
        console.log("Fetched stock medicine data:", res);
        setMedicine(res);
        setMedicineMap(
          res.reduce((acc: any, item: any) => {
            acc[item.id] = item;
            return acc;
          }, {})
        );
      })
      .catch((error) => {
        console.error("Error fetching medicine data:", error);
      });


      getAllPrescriptions().then((res)=>{
         console.table(res);
        setActions(res.map((item:any)=>({
          id: String(item.id),
          label: item.patientName,
          description: `Prescription by Dr. ${item.doctorName} on ${formatDate(item.prescriptionDate)}`,
          onClick:() => handleImport(item)
        })));
      }).catch((error)=>{
        console.error("Error fetching prescription data:", error);
      });
    fetchData();
  }, []);

  const handleImport = (item:any) => {

        setLoading(true);
        getMedicinesByPrescriptionId(item.id).then((res)=>{
         successNotification("Prescription imported successfully");
         setSaleItems(res);
         form.setValues({
          buyerName: item.patientName,
        
          saleItems: res.filter((x:any)=>x.medicineId != null).map((x:any)=>({medicineId: String(x.medicineId), quantity: calculateQuantity(x.frequency,x.duration)}))
         });

         
          console.log("res:", res);
        }).catch((error)=>{
          console.error("Error fetching medicines by prescription ID:", error);
          errorNotification(`Error fetching medicines: ${error.message}`);
        } ).finally(()=>setLoading(false));

    }
    const matches = useMediaQuery('(max-width: 768px)');

    const calculateQuantity = (freq:string, duration:number)=>{
      const freqValue = freqMap[freq];
      if(!freqValue) return 0;

      return Math.ceil(freqValue * duration );
  };

  const fetchData = () => {
    getAllsales()
      .then((res) => {
        console.log("Fetched sales data:", res);
        setData(res);
      })
      .catch((error) => {
        console.error("Error fetching sales data:", error);
      });
  };

  const handleSubmit = (values: any) => {
   let update = false;
    const saleItems = values.saleItems.map((x:any)=>({...x,unitPrice: medicineMap[x.medicineId]?.unitPrice
    }));

    const totalAmount = saleItems.reduce((acc:any, item:any) => acc + (item.unitPrice || 0) * item.quantity, 0);
    console.log("form submitted with value", saleItems);

    setLoading(true);

    addSale({...values, saleItems, totalAmount })
    
      .then((_res) => {

        successNotification("Sale added successfully");
        fetchData();
        form.reset();
        setEdit(false);
      })
      .catch((error) => {
        console.error("Error adding sale:", error);
        errorNotification(`Error adding sale: ${error.message}`);
      })
      .finally(() => setLoading(false));
      console.log("totalAmount", totalAmount);
  };

  const onGlobalFilterChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    let _filters: any = { ...filters };
    _filters["global"].value = value;
    setFilters(_filters);
    setGlobalFilterValue(value);
  };

  const handleExportCSV = () => {
    exportToCSV(
      "Sales_Export",
      ["Buyer Name", "Contact", "Total Amount (₹)", "Sale Date"],
      data.map((s: any) => [
        s.buyerName ?? "",
        s.buyerContact ?? "",
        s.totalAmount ?? 0,
        s.saleDate ? new Date(s.saleDate).toLocaleDateString("en-IN") : "",
      ])
    );
  };

  const [activeSale, setActiveSale] = useState<any>(null);

  const handleDetails = (rowData: any) => {
    open();
    setActiveSale(rowData);
    setLoading(true);
    getAllSaleItem(rowData.id)
      .then((res) => {
        setSaleItems(res);
      })
      .catch((error) => console.error("Error fetching sale items:", error))
      .finally(() => setLoading(false));
  };

  const onEdit = (rowData: any) => {
    setEdit(true);
    console.log("rowData:", rowData);
    form.setValues({
      ...rowData,
      name: rowData.name,
      dosage: rowData.dosage,
      category: rowData.category,
      type: rowData.type,
      manufacturer: rowData.manufacturer,
      unitPrice: rowData.unitPrice,
    });
  };


   const startToolbarTemplate = () => {
      return (
         <Button
            onClick={() => {
              form.reset(); // 👈 ensure add mode clears old id
              setEdit(true);
            }}
            variant="filled"
          >
            Sell Medicine
          </Button>
      )
    }
  
      const rightToolbarTemplate = () => {
         return <div className="md:flex hidden gap-3 items-center">
    
               <SegmentedControl
          value={view}
          size={matches ? "sm":"md"}
          color="primary"
          onChange={setView}
          data={[
            { label: <IconTable />, value: 'table' },
            { label: <IconLayoutGrid />, value: 'card' }
          
          ]}
        />
             <TextInput
             className="lg:block hidden"
              leftSection={<IconSearch
                 />}
              fw={400}
              value={globalFilterValue}
              onChange={onGlobalFilterChange}
              placeholder="Keyword Search"
            />
            <Button
              size="sm"
              variant="light"
              color="teal"
              leftSection={<IconDownload size={16} />}
              onClick={handleExportCSV}
            >
              Export CSV
            </Button>
           </div>
        };
  const renderSelectOption: SelectProps["renderOption"] = ({
    option,
    checked,
  }: any) => (
    <Group flex="1" gap="xs">
      <div className="flex gap-5 items-center">
        {option.label}
        {option?.manufacturer && (
          <span style={{ fontStyle: "italic", color: "gray" }}>
            {option.manufacturer}
            - {option.dosage}
          </span>
        )}
      </div>
      {checked && <IconCheck style={{ marginInlineStart: "auto" }} />}
    </Group>
  );

  const getRelativeDate = (dateStr: string) => {
    if (!dateStr) return "";
    const diff = Math.floor((Date.now() - new Date(dateStr).getTime()) / 86400000);
    if (diff === 0) return "Today";
    if (diff === 1) return "Yesterday";
    if (diff < 7) return `${diff}d ago`;
    if (diff < 30) return `${Math.floor(diff / 7)}w ago`;
    return `${Math.floor(diff / 30)}mo ago`;
  };

  const getTier = (amount: number) => {
    if (amount >= 1000) return { label: "Premium", color: "green" };
    if (amount >= 500) return { label: "Standard", color: "blue" };
    return { label: "Basic", color: "gray" };
  };

  const buyerBodyTemplate = (rowData: any) => (
    <Group gap={10} wrap="nowrap">
      <Avatar size="sm" radius="xl" name={rowData.buyerName} color="initials" variant="filled" />
      <div>
        <Text size="sm" fw={600} lineClamp={1}>{rowData.buyerName}</Text>
        <Group gap={4} mt={1}>
          <IconPhone size={10} className="text-gray-400" />
          <Text size="xs" c="dimmed">+91 {rowData.buyerContact}</Text>
        </Group>
      </div>
    </Group>
  );

  const amountBodyTemplate = (rowData: any) => {
    const amt = rowData.totalAmount ?? 0;
    const tier = getTier(amt);
    return (
      <Badge variant="light" color={tier.color} size="md" radius="md" leftSection={<span className="text-xs">₹</span>}>
        {amt.toLocaleString("en-IN")}
      </Badge>
    );
  };

  const tierBodyTemplate = (rowData: any) => {
    const tier = getTier(rowData.totalAmount ?? 0);
    return <Badge variant="dot" color={tier.color} size="sm">{tier.label}</Badge>;
  };

  const dateBodyTemplate = (rowData: any) => (
    <div>
      <Group gap={4}>
        <IconCalendar size={13} className="text-gray-400" />
        <Text size="sm" fw={500}>{formatDate(rowData.saleDate) ?? "—"}</Text>
      </Group>
      <Text size="xs" c="dimmed" mt={1}>{getRelativeDate(rowData.saleDate)}</Text>
    </div>
  );

  const actionBodyTemplate = (rowData: any) => (
    <Tooltip label="View Receipt" withArrow position="left">
      <Button
        size="xs"
        variant="light"
        color="teal"
        leftSection={<IconReceiptRupee size={14} />}
        onClick={() => handleDetails(rowData)}
        radius="xl"
      >
        Receipt
      </Button>
    </Tooltip>
  );

  const handleSpotlight = () => {
    spotlight.open();
  };
  return (
    <div>
      {!edit ? (
          <div>
            {/* Stats banner */}
            {data.length > 0 && (() => {
              const totalRevenue = data.reduce((s: number, sale: any) => s + (sale.totalAmount ?? 0), 0);
              const today = new Date().toDateString();
              const todaySales = data.filter((s: any) => new Date(s.saleDate).toDateString() === today);
              const avgSale = data.length > 0 ? Math.round(totalRevenue / data.length) : 0;
              return (
                <SimpleGrid cols={{ base: 2, sm: 4 }} spacing="sm" mb="lg">
                  {[
                    { label: "Total Revenue", value: `₹${totalRevenue.toLocaleString("en-IN")}`, icon: <IconReceiptRupee size={18} />, c: "teal", bg: "bg-teal-50" },
                    { label: "Today's Sales", value: todaySales.length, icon: <IconTrendingUp size={18} />, c: "blue", bg: "bg-blue-50" },
                    { label: "Transactions", value: data.length, icon: <IconShoppingCart size={18} />, c: "violet", bg: "bg-violet-50" },
                    { label: "Avg Sale", value: `₹${avgSale.toLocaleString("en-IN")}`, icon: <IconShoppingBag size={18} />, c: "orange", bg: "bg-orange-50" },
                  ].map((stat) => (
                    <Card key={stat.label} withBorder radius="md" p="md" shadow="xs" className={stat.bg}>
                      <Group justify="space-between">
                        <div>
                          <Text size="xs" c="dimmed" fw={500} tt="uppercase">{stat.label}</Text>
                          <Text size="xl" fw={700} c={`${stat.c}.7`}>{stat.value}</Text>
                        </div>
                        <ThemeIcon size="lg" radius="xl" variant="light" color={stat.c}>
                          {stat.icon}
                        </ThemeIcon>
                      </Group>
                    </Card>
                  ))}
                </SimpleGrid>
              );
            })()}

                        <Toolbar
                               className="mb-4 !p-1"
                                 start={startToolbarTemplate} 
                          
                               end={rightToolbarTemplate}></Toolbar>
                 
                       {view==="table"&&!matches? 
        <DataTable
          stripedRows
          size="small"
          value={data}
          paginator
          rows={10}
          filters={filters}
          globalFilterFields={["buyerName", "buyerContact", "totalAmount"]}
          paginatorTemplate="FirstPageLink PrevPageLink PageLinks NextPageLink LastPageLink CurrentPageReport RowsPerPageDropdown"
          rowsPerPageOptions={[10, 25, 50]}
          dataKey="id"
          filterDisplay="menu"
          emptyMessage={
            <div className="flex flex-col items-center py-10 gap-3">
              <IconShoppingCart size={48} stroke={1.2} className="text-gray-300" />
              <Text c="dimmed" fw={500}>No sales transactions yet.</Text>
            </div>
          }
          currentPageReportTemplate="Showing {first} to {last} of {totalRecords} entries"
        >
          <Column
            header="Buyer"
            body={buyerBodyTemplate}
            sortable
            sortField="buyerName"
            style={{ minWidth: "200px" }}
          />
          <Column
            header="Amount"
            body={amountBodyTemplate}
            sortable
            sortField="totalAmount"
            style={{ minWidth: "130px" }}
          />
          <Column
            header="Tier"
            body={tierBodyTemplate}
            style={{ minWidth: "110px" }}
          />
          <Column
            header="Sale Date"
            body={dateBodyTemplate}
            sortable
            sortField="saleDate"
            style={{ minWidth: "150px" }}
          />
          <Column header="Actions" body={actionBodyTemplate} style={{ minWidth: "120px" }} />
  
        </DataTable>
      
          : <div>
              {data?.length === 0 ? (
                <div className="flex flex-col items-center justify-center py-20 gap-4">
                  <div className="w-20 h-20 rounded-full bg-gray-100 flex items-center justify-center">
                    <IconShoppingCart size={40} stroke={1.2} className="text-gray-300" />
                  </div>
                  <Text fw={600} c="dimmed" size="lg">No sales yet</Text>
                  <Text size="sm" c="dimmed">Create your first sale using the "Sell Medicine" button above.</Text>
                </div>
              ) : (
                <div className="grid lg:grid-cols-3 md:grid-cols-2 sm:grid-cols-2 grid-cols-1 gap-4">
                  {data.map((app) => (
                    <SaleCard
                      key={app.id}
                      id={app.id}
                      onView={() => handleDetails(app)}
                      buyerName={app.buyerName}
                      buyerContact={app.buyerContact}
                      saleDate={app.saleDate}
                      totalAmount={app.totalAmount}
                    />
                  ))}
                </div>
              )}
            </div>}
                      </div>) : (<div>

        <div className="mb-5 flex items-center justify-between">
            <h3 className="text-xl font-medium text-primary-500" >Sell Medicine</h3>

            <Button variant="filled" leftSection={<IconPlus />} onClick={handleSpotlight} >
              Import Prescription
            </Button>

        </div>
        <form onSubmit={form.onSubmit(handleSubmit)}>
          <LoadingOverlay visible={loading} />
             <Fieldset
            className="grid gap-5"
            legend={
              <span className="text-lg font-medium text-primary-500">
                Buyer information
              </span>
            }
            radius="md"
          >
          <div className="grid gap-8 sm:grid-cols-2">
              <TextInput
              withAsterisk
              placeholder="Enter Buyer Name"
              label="Buyer Name"
              {...form.getInputProps("buyerName")}
            />
            <NumberInput
            maxLength={10}

              withAsterisk
              label="Buyer Contact"
              placeholder="Enter Buyer Contact Number"
              {...form.getInputProps("buyerContact")}
            />
          </div>

          </Fieldset>
          <Fieldset
            className="grid gap-5"
            legend={
              <span className="text-lg font-medium text-primary-500">
                Medicine information
              </span>
            }
            radius="md"
          >
            <div className="grid gap-4 sm:grid-cols-5">
              {form.values.saleItems.map((item: any, index: number) => (
                <React.Fragment key={index}>
                  <div className="col-span-2">
                    <Select
                      renderOption={renderSelectOption}
                      {...form.getInputProps(`saleItems.${index}.medicineId`)}
                      label="Medicine"
                      placeholder="Select Medicine"
                      data={Medicine.filter(x=>!form.values.saleItems.some((item1:any,idx:any )=>item1.medicineId ===x.id &&idx!==index)).map((item) => ({
                        ...item,
                        value: "" + item.id,
                        label: item.name,
                      }))}
                    />
                  </div>

                  <div className="col-span-2">
                    <NumberInput rightSectionWidth={80} rightSection={<div className="text-xs flex gap-1 text-gray-500">Stock: {medicineMap[item.medicineId]?.stock || 0}</div>}
                      {...form.getInputProps(`saleItems.${index}.quantity`)}
                      label="Quantity"
                      placeholder="Enter Quantity in stock"
                      min={0}
                      max={medicineMap[item.medicineId]?.stock || 0}
                      clampBehavior="strict"
                    />
                  </div>

                  <div  className="flex items-center justify-between">

                     {(item.quantity && item.medicineId) ?<div>
                        Total Price: ₹{item.quantity} X{medicineMap[item.medicineId]?.unitPrice} = ₹
                        {item.quantity *
                          (medicineMap[item.medicineId]?.unitPrice ?? 0)}
                      </div> : null}
                    <ActionIcon color="red" onClick={() => form.removeListItem("saleItems", index)}>

<IconTrash size={16}/>

                    </ActionIcon>
                  </div>
                </React.Fragment>
              ))}
            </div>

            <div className="flex justify-center items-center">
              <Button
              onClick={addMore}
              variant="outline" leftSection={<IconPlus />}>
                
                Add More
              </Button>
            </div>
          </Fieldset>

          <div className="flex items-center gap-5 justify-center mt-4">
            <Button
              loading={loading}
              type="submit"
              className="w-full"
              variant="filled"
              color="primary"
            >
             Sell Medicine
            </Button>
            <Button
              loading={loading}
              className="w-full"
              variant="filled"
              color="red"
              onClick={() => {
                setEdit(false);
              }}
            >
              Cancel
            </Button>
          </div>
        </form>
        </div>
      )}
           <Modal opened={opened} size="lg" onClose={close} title={
        <Group gap={8}>
          <ThemeIcon size="md" radius="xl" variant="gradient" gradient={{ from: "teal", to: "blue" }}>
            <IconReceiptRupee size={16} />
          </ThemeIcon>
          <Text fw={700} size="lg">Sale Receipt</Text>
        </Group>
      } centered>
        <LoadingOverlay visible={loading} />

        {/* Receipt items */}
        {saleItems.length === 0 ? (
          <Text c="dimmed" size="sm" ta="center" py="xl">No medicines in this sale.</Text>
        ) : (
          <div className="border rounded-xl overflow-hidden">
            {/* Header row */}
            <div className="grid grid-cols-12 bg-gray-50 border-b px-4 py-2">
              <Text size="xs" fw={700} c="dimmed" className="col-span-5">MEDICINE</Text>
              <Text size="xs" fw={700} c="dimmed" className="col-span-2 text-center">QTY</Text>
              <Text size="xs" fw={700} c="dimmed" className="col-span-2 text-right">UNIT</Text>
              <Text size="xs" fw={700} c="dimmed" className="col-span-3 text-right">TOTAL</Text>
            </div>

            {/* Item rows */}
            {saleItems.map((item: any, index: number) => {
              const med = medicineMap[item.medicineId];
              const lineTotal = (item.quantity ?? 0) * (item.unitPrice ?? 0);
              return (
                <div key={index} className={`grid grid-cols-12 px-4 py-3 items-center ${index % 2 === 0 ? "bg-white" : "bg-gray-50/50"}`}>
                  <div className="col-span-5">
                    <Text size="sm" fw={600} lineClamp={1}>{med?.name ?? `Medicine #${item.medicineId}`}</Text>
                    <Text size="xs" c="dimmed">{med?.dosage ?? ""} {med?.manufacturer ? `· ${med.manufacturer}` : ""}</Text>
                  </div>
                  <Text size="sm" className="col-span-2 text-center" c="dimmed">{item.quantity}</Text>
                  <Text size="sm" className="col-span-2 text-right" c="dimmed">₹{item.unitPrice ?? 0}</Text>
                  <Text size="sm" fw={700} className="col-span-3 text-right" c="teal.7">₹{lineTotal.toLocaleString("en-IN")}</Text>
                </div>
              );
            })}

            {/* Total footer */}
            <div className="border-t-2 border-dashed px-4 py-3 bg-teal-50 flex justify-between items-center">
              <Group gap={6}>
                <IconReceiptRupee size={16} className="text-teal-600" />
                <Text fw={700} c="teal.7">Grand Total</Text>
              </Group>
              <Text size="lg" fw={900} c="teal.7">
                ₹{saleItems.reduce((s: number, i: any) => s + (i.quantity ?? 0) * (i.unitPrice ?? 0), 0).toLocaleString("en-IN")}
              </Text>
            </div>
          </div>
        )}

        {/* Download PDF button */}
        {saleItems.length > 0 && activeSale && (
          <div className="mt-4 flex justify-end">
            <Button
              variant="gradient"
              gradient={{ from: "teal", to: "blue" }}
              leftSection={<IconFileTypePdf size={16} />}
              onClick={() => generateReceiptPDF(activeSale, saleItems, medicineMap)}
            >
              Download PDF
            </Button>
          </div>
        )}
      </Modal>
      <Spotlight
        actions={actions}
        nothingFound="Nothing found..."
        highlightQuery
        searchProps={{
          leftSection: <IconSearch size={20} stroke={1.5} />,
          placeholder: 'Search...',
        }}
      />
  
      </div>
  );
};

export default Sales;
