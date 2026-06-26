import jsPDF from "jspdf";
import autoTable from "jspdf-autotable";

// ─── CSV ──────────────────────────────────────────────────────────────────────

export function exportToCSV(filename: string, headers: string[], rows: (string | number)[][]) {
  const csvContent = [
    headers.join(","),
    ...rows.map((row) =>
      row.map((cell) => `"${String(cell ?? "").replace(/"/g, '""')}"`).join(",")
    ),
  ].join("\n");

  const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
  const url = URL.createObjectURL(blob);
  const link = document.createElement("a");
  link.href = url;
  link.download = `${filename}_${new Date().toISOString().slice(0, 10)}.csv`;
  link.click();
  URL.revokeObjectURL(url);
}

// ─── PDF Helpers ──────────────────────────────────────────────────────────────

function addHospitalHeader(doc: jsPDF) {
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFillColor(14, 165, 233); // sky-500
  doc.rect(0, 0, pageWidth, 28, "F");

  doc.setTextColor(255, 255, 255);
  doc.setFontSize(18);
  doc.setFont("helvetica", "bold");
  doc.text("Pulse HMS", 14, 12);

  doc.setFontSize(9);
  doc.setFont("helvetica", "normal");
  doc.text("Hospital Management System", 14, 19);

  const now = new Date().toLocaleString("en-IN", {
    day: "2-digit", month: "short", year: "numeric",
    hour: "2-digit", minute: "2-digit",
  });
  doc.text(`Generated: ${now}`, pageWidth - 14, 19, { align: "right" });

  doc.setTextColor(30, 41, 59); // reset to dark
}

function addFooter(doc: jsPDF, text = "Pulse HMS — Confidential") {
  const pages = (doc as any).internal.pages.length - 1;
  for (let i = 1; i <= pages; i++) {
    doc.setPage(i);
    const pageWidth = doc.internal.pageSize.getWidth();
    const pageHeight = doc.internal.pageSize.getHeight();
    doc.setFontSize(8);
    doc.setTextColor(150);
    doc.text(text, 14, pageHeight - 8);
    doc.text(`Page ${i} of ${pages}`, pageWidth - 14, pageHeight - 8, { align: "right" });
  }
}

// ─── Receipt PDF ──────────────────────────────────────────────────────────────

interface ReceiptItem {
  medicineId: number | string;
  quantity: number;
  unitPrice: number;
}

export function generateReceiptPDF(
  sale: { id: number; buyerName: string; buyerContact: string; saleDate: string },
  items: ReceiptItem[],
  medicineMap: Record<string, any>
) {
  const doc = new jsPDF();
  addHospitalHeader(doc);

  // Receipt title
  doc.setFontSize(14);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("SALE RECEIPT", 105, 38, { align: "center" });

  // Buyer info box
  doc.setFillColor(241, 245, 249);
  doc.roundedRect(14, 43, 182, 26, 3, 3, "F");

  doc.setFontSize(9);
  doc.setFont("helvetica", "bold");
  doc.text("BUYER INFORMATION", 20, 50);

  doc.setFont("helvetica", "normal");
  doc.text(`Name: ${sale.buyerName}`, 20, 57);
  doc.text(`Contact: +91 ${sale.buyerContact}`, 20, 63);
  doc.text(`TXN ID: #${String(sale.id).padStart(6, "0")}`, 120, 57);
  doc.text(`Date: ${new Date(sale.saleDate).toLocaleDateString("en-IN", { day: "2-digit", month: "short", year: "numeric" })}`, 120, 63);

  // Items table
  const tableRows = items.map((item) => {
    const med = medicineMap[item.medicineId];
    const name = med?.name ?? `Medicine #${item.medicineId}`;
    const dosage = med?.dosage ?? "";
    const total = (item.quantity ?? 0) * (item.unitPrice ?? 0);
    return [
      `${name}${dosage ? ` (${dosage})` : ""}`,
      item.quantity,
      `₹${item.unitPrice ?? 0}`,
      `₹${total.toLocaleString("en-IN")}`,
    ];
  });

  const grandTotal = items.reduce((s, i) => s + (i.quantity ?? 0) * (i.unitPrice ?? 0), 0);

  autoTable(doc, {
    startY: 74,
    head: [["Medicine", "Qty", "Unit Price", "Total"]],
    body: tableRows,
    foot: [["", "", "Grand Total", `₹${grandTotal.toLocaleString("en-IN")}`]],
    headStyles: { fillColor: [14, 165, 233], textColor: 255, fontStyle: "bold" },
    footStyles: { fillColor: [220, 252, 231], textColor: [22, 101, 52], fontStyle: "bold" },
    alternateRowStyles: { fillColor: [248, 250, 252] },
    columnStyles: { 3: { halign: "right" }, 2: { halign: "right" }, 1: { halign: "center" } },
    margin: { left: 14, right: 14 },
  });

  // Thank you note
  const finalY = (doc as any).lastAutoTable.finalY + 8;
  doc.setFontSize(9);
  doc.setTextColor(100, 116, 139);
  doc.setFont("helvetica", "italic");
  doc.text("Thank you for choosing Pulse HMS. Get well soon!", 105, finalY, { align: "center" });

  addFooter(doc, "Pulse HMS — Sale Receipt");
  doc.save(`Receipt_${String(sale.id).padStart(6, "0")}_${sale.buyerName.replace(/\s+/g, "_")}.pdf`);
}

// ─── Stock Report PDF ─────────────────────────────────────────────────────────

interface StockItem {
  id: number;
  medicineId: number | string;
  batchNo: string;
  quantity: number;
  initialQuantity: number;
  expiryDate: string;
  status: string;
}

export function generateStockReportPDF(stock: StockItem[], medicineMap: Record<string, any>) {
  const doc = new jsPDF({ orientation: "landscape" });
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(15);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("MEDICINE STOCK REPORT", pageWidth / 2, 38, { align: "center" });

  const total = stock.length;
  const lowStock = stock.filter((s) => (s.quantity ?? 0) <= 10 && s.status !== "EXPIRED").length;
  const expired = stock.filter((s) => s.status === "EXPIRED").length;
  const active = stock.filter((s) => s.status === "ACTIVE" && (s.quantity ?? 0) > 10).length;

  const summaries = [
    { label: "Total Items", value: total, color: [59, 130, 246] as [number, number, number] },
    { label: "Active & Healthy", value: active, color: [34, 197, 94] as [number, number, number] },
    { label: "Low Stock (≤10)", value: lowStock, color: [249, 115, 22] as [number, number, number] },
    { label: "Expired", value: expired, color: [239, 68, 68] as [number, number, number] },
  ];

  const cardW = (pageWidth - 28) / summaries.length;
  let x = 14;
  summaries.forEach((s) => {
    doc.setFillColor(...s.color);
    doc.roundedRect(x, 43, cardW - 3, 18, 3, 3, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(16);
    doc.setFont("helvetica", "bold");
    doc.text(String(s.value), x + (cardW - 3) / 2, 54, { align: "center" });
    doc.setFontSize(7);
    doc.setFont("helvetica", "normal");
    doc.text(s.label, x + (cardW - 3) / 2, 59, { align: "center" });
    x += cardW;
  });

  // Items table
  const tableRows = stock.map((item) => {
    const med = medicineMap[item.medicineId];
    const qty = item.quantity ?? 0;
    const isLow = qty <= 10 && item.status !== "EXPIRED";
    const isExpired = item.status === "EXPIRED";
    return {
      row: [
        med?.name ?? `Medicine #${item.medicineId}`,
        med?.manufacturer ?? "—",
        item.batchNo ?? "—",
        qty,
        item.initialQuantity ?? "—",
        item.expiryDate ? new Date(item.expiryDate).toLocaleDateString("en-IN") : "—",
        item.status ?? "—",
      ],
      isLow,
      isExpired,
    };
  });

  autoTable(doc, {
    startY: 66,
    head: [["Medicine", "Manufacturer", "Batch No", "Current Qty", "Initial Qty", "Expiry Date", "Status"]],
    body: tableRows.map((t) => t.row),
    headStyles: { fillColor: [14, 165, 233], textColor: 255, fontStyle: "bold" },
    alternateRowStyles: { fillColor: [248, 250, 252] },
    columnStyles: { 3: { halign: "center" }, 4: { halign: "center" } },
    margin: { left: 14, right: 14 },
    didParseCell: (data: any) => {
      const rowIdx = data.row.index;
      if (data.section === "body" && rowIdx < tableRows.length) {
        const { isLow, isExpired } = tableRows[rowIdx];
        if (isExpired) {
          data.cell.styles.fillColor = [254, 226, 226];
          data.cell.styles.textColor = [153, 27, 27];
        } else if (isLow) {
          data.cell.styles.fillColor = [255, 237, 213];
          data.cell.styles.textColor = [154, 52, 18];
        }
      }
    },
  });

  addFooter(doc, "Pulse HMS — Stock Report (Confidential)");
  doc.save(`Stock_Report_${new Date().toISOString().slice(0, 10)}.pdf`);
}

// ─── Doctor Report PDF ────────────────────────────────────────────────────────

export function generateDoctorReportPDF(doctors: any[]) {
  const doc = new jsPDF({ orientation: "landscape" });
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(15);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("DOCTOR DIRECTORY REPORT", pageWidth / 2, 38, { align: "center" });

  const depts = Array.from(new Set(doctors.map((d) => d.department).filter(Boolean))).length;
  const specs = Array.from(new Set(doctors.map((d) => d.specialization).filter(Boolean))).length;

  const summaries: [string, number | string, [number, number, number]][] = [
    ["Total Doctors", doctors.length, [59, 130, 246]],
    ["Departments", depts, [34, 197, 94]],
    ["Specializations", specs, [168, 85, 247]],
  ];
  const cardW = (pageWidth - 28) / summaries.length;
  let x = 14;
  summaries.forEach(([label, value, color]) => {
    doc.setFillColor(...color);
    doc.roundedRect(x, 43, cardW - 3, 16, 3, 3, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(14);
    doc.setFont("helvetica", "bold");
    doc.text(String(value), x + (cardW - 3) / 2, 52, { align: "center" });
    doc.setFontSize(7);
    doc.setFont("helvetica", "normal");
    doc.text(label, x + (cardW - 3) / 2, 57, { align: "center" });
    x += cardW;
  });

  autoTable(doc, {
    startY: 64,
    head: [["Name", "Email", "Phone", "Department", "Specialization", "Address", "Experience"]],
    body: doctors.map((d) => [
      d.name ?? "—",
      d.email ?? "—",
      d.phone ? `+91 ${d.phone}` : "—",
      d.department ?? "—",
      d.specialization ?? "—",
      d.address ?? "—",
      d.totalExp ? `${d.totalExp} yrs` : "—",
    ]),
    headStyles: { fillColor: [59, 130, 246], textColor: 255, fontStyle: "bold" },
    alternateRowStyles: { fillColor: [239, 246, 255] },
    margin: { left: 14, right: 14 },
  });

  addFooter(doc, "Pulse HMS — Doctor Directory (Confidential)");
  doc.save(`Doctor_Report_${new Date().toISOString().slice(0, 10)}.pdf`);
}

// ─── Patient Report PDF ───────────────────────────────────────────────────────

export function generatePatientReportPDF(patients: any[], bloodGroupMap: Record<string, string> = {}) {
  const doc = new jsPDF({ orientation: "landscape" });
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(15);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("PATIENT DIRECTORY REPORT", pageWidth / 2, 38, { align: "center" });

  const bloodGroups = Array.from(new Set(patients.map((p) => p.bloodGroup).filter(Boolean))).length;

  const summaries: [string, number | string, [number, number, number]][] = [
    ["Total Patients", patients.length, [16, 185, 129]],
    ["Blood Groups", bloodGroups, [245, 158, 11]],
    ["With Chronic Disease", patients.filter((p) => p.chronicDisease).length, [239, 68, 68]],
  ];
  const cardW = (pageWidth - 28) / summaries.length;
  let x = 14;
  summaries.forEach(([label, value, color]) => {
    doc.setFillColor(...color);
    doc.roundedRect(x, 43, cardW - 3, 16, 3, 3, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(14);
    doc.setFont("helvetica", "bold");
    doc.text(String(value), x + (cardW - 3) / 2, 52, { align: "center" });
    doc.setFontSize(7);
    doc.setFont("helvetica", "normal");
    doc.text(label, x + (cardW - 3) / 2, 57, { align: "center" });
    x += cardW;
  });

  autoTable(doc, {
    startY: 64,
    head: [["Name", "Email", "Phone", "DOB", "Blood Group", "Address", "Allergies", "Chronic Disease"]],
    body: patients.map((p) => [
      p.name ?? "—",
      p.email ?? "—",
      p.phone ? `+91 ${p.phone}` : "—",
      p.dob ?? "—",
      bloodGroupMap[p.bloodGroup] ?? p.bloodGroup ?? "—",
      p.address ?? "—",
      p.allergies ?? "None",
      p.chronicDisease ?? "None",
    ]),
    headStyles: { fillColor: [16, 185, 129], textColor: 255, fontStyle: "bold" },
    alternateRowStyles: { fillColor: [236, 253, 245] },
    margin: { left: 14, right: 14 },
    columnStyles: { 6: { cellWidth: 30 }, 7: { cellWidth: 35 } },
  });

  addFooter(doc, "Pulse HMS — Patient Directory (Confidential)");
  doc.save(`Patient_Report_${new Date().toISOString().slice(0, 10)}.pdf`);
}

// ─── Medicine Catalogue PDF ───────────────────────────────────────────────────

export function generateMedicineReportPDF(medicines: any[]) {
  const doc = new jsPDF({ orientation: "landscape" });
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(15);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("MEDICINE CATALOGUE REPORT", pageWidth / 2, 38, { align: "center" });

  const totalValue = medicines.reduce((s, m) => s + (m.unitPrice ?? 0) * (m.stock ?? 0), 0);
  const categories = Array.from(new Set(medicines.map((m) => m.category).filter(Boolean))).length;

  const summaries: [string, string | number, [number, number, number]][] = [
    ["Total Medicines", medicines.length, [139, 92, 246]],
    ["Categories", categories, [59, 130, 246]],
    ["Total Inventory Value", `Rs ${totalValue.toLocaleString("en-IN")}`, [16, 185, 129]],
  ];
  const cardW = (pageWidth - 28) / summaries.length;
  let x = 14;
  summaries.forEach(([label, value, color]) => {
    doc.setFillColor(...color);
    doc.roundedRect(x, 43, cardW - 3, 16, 3, 3, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(label === "Total Inventory Value" ? 9 : 14);
    doc.setFont("helvetica", "bold");
    doc.text(String(value), x + (cardW - 3) / 2, 52, { align: "center" });
    doc.setFontSize(7);
    doc.setFont("helvetica", "normal");
    doc.text(label, x + (cardW - 3) / 2, 57, { align: "center" });
    x += cardW;
  });

  autoTable(doc, {
    startY: 64,
    head: [["Name", "Dosage", "Category", "Type", "Manufacturer", "Unit Price (Rs)", "Stock"]],
    body: medicines.map((m) => [
      m.name ?? "—",
      m.dosage ?? "—",
      m.category ?? "—",
      m.type ?? "—",
      m.manufacturer ?? "—",
      m.unitPrice ?? 0,
      m.stock ?? 0,
    ]),
    headStyles: { fillColor: [139, 92, 246], textColor: 255, fontStyle: "bold" },
    alternateRowStyles: { fillColor: [245, 243, 255] },
    columnStyles: { 5: { halign: "right" }, 6: { halign: "center" } },
    margin: { left: 14, right: 14 },
  });

  addFooter(doc, "Pulse HMS — Medicine Catalogue (Confidential)");
  doc.save(`Medicine_Catalogue_${new Date().toISOString().slice(0, 10)}.pdf`);
}

// ─── Appointment Report PDF (Doctor) ─────────────────────────────────────────

export function generateAppointmentReportPDF(appointments: any[], title = "APPOINTMENT REPORT") {
  const doc = new jsPDF({ orientation: "landscape" });
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(15);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text(title, pageWidth / 2, 38, { align: "center" });

  const total = appointments.length;
  const completed = appointments.filter((a) => a.status === "COMPLETED").length;
  const cancelled = appointments.filter((a) => a.status === "CANCELLED").length;
  const scheduled = appointments.filter((a) => a.status === "SCHEDULED").length;

  const summaries: [string, number, [number, number, number]][] = [
    ["Total", total, [59, 130, 246]],
    ["Scheduled", scheduled, [245, 158, 11]],
    ["Completed", completed, [34, 197, 94]],
    ["Cancelled", cancelled, [239, 68, 68]],
  ];
  const cardW = (pageWidth - 28) / summaries.length;
  let x = 14;
  summaries.forEach(([label, value, color]) => {
    doc.setFillColor(...color);
    doc.roundedRect(x, 43, cardW - 3, 16, 3, 3, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(14);
    doc.setFont("helvetica", "bold");
    doc.text(String(value), x + (cardW - 3) / 2, 52, { align: "center" });
    doc.setFontSize(7);
    doc.setFont("helvetica", "normal");
    doc.text(label, x + (cardW - 3) / 2, 57, { align: "center" });
    x += cardW;
  });

  autoTable(doc, {
    startY: 64,
    head: [["Patient", "Phone", "Appointment Time", "Reason", "Notes", "Status"]],
    body: appointments.map((a) => [
      a.patientName ?? "—",
      a.patientPhone ? `+91 ${a.patientPhone}` : "—",
      a.appointmentTime ? new Date(a.appointmentTime).toLocaleString("en-IN") : "—",
      a.reason ?? "—",
      a.notes ?? "—",
      a.status ?? "—",
    ]),
    headStyles: { fillColor: [59, 130, 246], textColor: 255, fontStyle: "bold" },
    alternateRowStyles: { fillColor: [239, 246, 255] },
    margin: { left: 14, right: 14 },
    didParseCell: (data: any) => {
      if (data.section === "body" && data.column.index === 5) {
        const status = data.cell.raw;
        if (status === "COMPLETED") { data.cell.styles.textColor = [21, 128, 61]; data.cell.styles.fontStyle = "bold"; }
        else if (status === "CANCELLED") { data.cell.styles.textColor = [185, 28, 28]; data.cell.styles.fontStyle = "bold"; }
        else if (status === "SCHEDULED") { data.cell.styles.textColor = [146, 64, 14]; }
      }
    },
  });

  addFooter(doc, "Pulse HMS — Appointment Report (Confidential)");
  doc.save(`Appointment_Report_${new Date().toISOString().slice(0, 10)}.pdf`);
}

// ─── Medical Records PDF (Patient) ───────────────────────────────────────────

export function generateMedicalRecordsPDF(records: any[], patientName = "Patient") {
  const doc = new jsPDF();
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(14);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("MEDICAL RECORDS", pageWidth / 2, 36, { align: "center" });
  doc.setFontSize(9);
  doc.setFont("helvetica", "normal");
  doc.setTextColor(100);
  doc.text(`Patient: ${patientName}`, pageWidth / 2, 43, { align: "center" });

  let yPos = 52;

  records.forEach((record: any, idx: number) => {
    // Section header
    doc.setFillColor(14, 165, 233);
    doc.roundedRect(14, yPos, pageWidth - 28, 8, 2, 2, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(9);
    doc.setFont("helvetica", "bold");
    doc.text(`Visit #${idx + 1}  —  Dr. ${record.doctorName ?? "N/A"}`, 18, yPos + 5.5);
    yPos += 12;

    // Details table
    autoTable(doc, {
      startY: yPos,
      body: [
        ["Diagnosis", record.diagnosis ?? "—"],
        ["Symptoms", Array.isArray(record.symptoms) ? record.symptoms.join(", ") : record.symptoms ?? "—"],
        ["Notes", record.notes ?? "—"],
        ["Referral", record.referral && record.referral !== "None" ? record.referral : "None"],
        ["Created", record.createdAt ? new Date(record.createdAt).toLocaleDateString("en-IN") : "—"],
        ["Follow-up", record.followUpDate ?? "—"],
      ],
      columnStyles: { 0: { fontStyle: "bold", cellWidth: 38, fillColor: [241, 245, 249] }, 1: { cellWidth: "auto" } },
      bodyStyles: { fontSize: 8, cellPadding: 3 },
      alternateRowStyles: { fillColor: [248, 250, 252] },
      margin: { left: 14, right: 14 },
    });

    yPos = (doc as any).lastAutoTable.finalY + 8;

    if (yPos > 255 && idx < records.length - 1) {
      doc.addPage();
      yPos = 20;
    }
  });

  addFooter(doc, `Pulse HMS — Medical Records: ${patientName}`);
  doc.save(`Medical_Records_${new Date().toISOString().slice(0, 10)}.pdf`);
}

// ─── Prescriptions PDF (Patient) ─────────────────────────────────────────────

export function generatePrescriptionsPDF(prescriptions: any[], patientName = "Patient") {
  const doc = new jsPDF({ orientation: "landscape" });
  addHospitalHeader(doc);
  const pageWidth = doc.internal.pageSize.getWidth();

  doc.setFontSize(14);
  doc.setFont("helvetica", "bold");
  doc.setTextColor(30, 41, 59);
  doc.text("PRESCRIPTIONS REPORT", pageWidth / 2, 36, { align: "center" });
  doc.setFontSize(9);
  doc.setFont("helvetica", "normal");
  doc.setTextColor(100);
  doc.text(`Patient: ${patientName}  |  Total: ${prescriptions.length} prescription(s)`, pageWidth / 2, 43, { align: "center" });

  let yPos = 50;

  prescriptions.forEach((p: any, idx: number) => {
    const date = p.prescriptionDate
      ? new Date(p.prescriptionDate).toLocaleDateString("en-IN")
      : p.appointmentTime
      ? new Date(p.appointmentTime).toLocaleDateString("en-IN")
      : "—";

    // Prescription header bar
    doc.setFillColor(139, 92, 246);
    doc.roundedRect(14, yPos, pageWidth - 28, 8, 2, 2, "F");
    doc.setTextColor(255, 255, 255);
    doc.setFontSize(9);
    doc.setFont("helvetica", "bold");
    doc.text(
      `Prescription #${idx + 1}  (ID: ${p.id})   |   Dr. ${p.doctorName ?? "N/A"}   |   Date: ${date}`,
      18, yPos + 5.5
    );
    yPos += 11;

    const medicines: any[] = Array.isArray(p.medicines) ? p.medicines : [];

    if (medicines.length === 0) {
      doc.setFontSize(8);
      doc.setFont("helvetica", "italic");
      doc.setTextColor(150);
      doc.text("No medicines found for this prescription.", 18, yPos + 5);
      yPos += 12;
    } else {
      autoTable(doc, {
        startY: yPos,
        head: [["Medicine Name", "Dosage", "Duration", "Instructions"]],
        body: medicines.map((med: any) => [
          med.medicineName || med.name || med.medicine || "—",
          med.dosage || "—",
          med.duration || "—",
          med.instructions || "—",
        ]),
        headStyles: { fillColor: [167, 139, 250], textColor: 255, fontStyle: "bold", fontSize: 8 },
        bodyStyles: { fontSize: 8, cellPadding: 2.5 },
        alternateRowStyles: { fillColor: [245, 243, 255] },
        margin: { left: 14, right: 14 },
      });
      yPos = (doc as any).lastAutoTable.finalY + 6;
    }

    if (yPos > 175 && idx < prescriptions.length - 1) {
      doc.addPage();
      yPos = 20;
    }
  });

  addFooter(doc, `Pulse HMS — Prescriptions: ${patientName}`);
  doc.save(`Prescriptions_${new Date().toISOString().slice(0, 10)}.pdf`);
}
